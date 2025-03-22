import { apiGet } from "@/modules/api/apiWrapper";
import { ApiResponse } from "@/modules/api/types";
import { ApiWorkspaceTag, Organization } from "../../domain/types";

async function listOrganizations(): Promise<ApiResponse<Organization[]>> {
  return await apiGet("/api/v1/organization", { dataWrapped: true });
}

async function listOrganizationTags(organizationId: string): Promise<ApiResponse<ApiWorkspaceTag[]>> {
  return await apiGet(`/api/v1/organization/${organizationId}/tag`, { dataWrapped: true });
}

const methods = {
  listOrganizations,
  listOrganizationTags,
};

export default methods;
