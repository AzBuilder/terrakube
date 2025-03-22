import { apiPost } from "@/modules/api/apiWrapper";
import { ApiResponse } from "@/modules/api/types";
import { ListWorkspacesResponse, WorkspaceListItem } from "@/modules/workspaces/types";
import fixSshUrl from "@/modules/workspaces/utils/fixSshUrl";

async function listWorkspaces(organizationId: string): Promise<ApiResponse<ListWorkspacesResponse>> {
  const body = {
    query: `{
          organization(ids: ["${organizationId}"]) {
            edges {
              node {
                id
                name
                workspace(sort: "name") {
                  edges {
                    node {
                      id
                      name
                      description
                      source
                      branch
                      terraformVersion
                      iacType
                      workspaceTag {
                        edges { 
                          node {
                            id
                            tagId
                          }
                        } 
                      }
                      job(sort: "id") {
                        edges {
                          node {
                            id
                            status
                            updatedDate
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }`,
  };

  const tempData = await apiPost<unknown, any>("/graphql/api/v1", body, {
    dataWrapped: true,
    contentType: "application/json",
  });

  if (tempData.isError) {
    return {
      isError: tempData.isError,
      responseCode: tempData.responseCode,
      error: tempData.error,
      originResponseCode: tempData.originResponseCode,
      data: {
        organizationId: "",
        organizationName: "",
        workspaces: [],
      },
    };
  }
  const organization = tempData.data.organization.edges[0].node;
  const includes = tempData.data.organization.edges[0].node.workspace.edges;

  const workspaces = includes.map((element: any) => {
    const lastJob = element.node.job.edges?.slice(-1)?.pop()?.node;
    const lastStatus = lastJob?.status ?? "NeverExecuted";
    const ws: WorkspaceListItem = {
      id: element.node.id,
      lastRun: lastJob?.updatedDate,
      lastStatus,
      name: element.node.name,
      description: element.node.description,
      branch: element.node.branch,
      iacType: element.node.iacType,
      source: element.node.source,
      normalizedSource: fixSshUrl(element.node.source),
      terraformVersion: element.node.terraformVersion,
      tags: element.node?.workspaceTag?.edges?.map((e: any) => e.node.tagId),
    };
    return ws;
  });

  return {
    isError: tempData.isError,
    responseCode: tempData.responseCode,
    error: tempData.error,
    originResponseCode: tempData.originResponseCode,
    data: {
      organizationId: organization?.id,
      organizationName: organization?.name,
      workspaces,
    },
  };
}

const methods = {
  listWorkspaces,
};

export default methods;
