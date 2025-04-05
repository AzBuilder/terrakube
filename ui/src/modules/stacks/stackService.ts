import { apiGet } from "@/modules/api/apiWrapper";
import { ApiResponse } from "@/modules/api/types";
import { ListStacksResponse, StackListItem } from "@/modules/stacks/types";

async function listStacks(organizationId: string): Promise<ApiResponse<ListStacksResponse>> {
  const response = await apiGet<{ data: StackListItem[] }>(`/api/v1/organization/${organizationId}/stacks`, {
    dataWrapped: true,
    contentType: "application/vnd.api+json",
  });

  if (response.isError) {
    return {
      isError: response.isError,
      responseCode: response.responseCode,
      error: response.error,
      originResponseCode: response.originResponseCode,
      data: {
        organizationId: "",
        organizationName: "",
        stacks: [],
      },
    };
  }

  // Get organization name
  const orgResponse = await apiGet<{ data: { attributes: { name: string } } }>(`/api/v1/organization/${organizationId}`, {
    dataWrapped: true,
    contentType: "application/vnd.api+json",
  });

  return {
    isError: false,
    responseCode: response.responseCode,
    error: null,
    originResponseCode: response.originResponseCode,
    data: {
      organizationId,
      organizationName: orgResponse.data?.data?.attributes?.name || "",
      stacks: response.data?.data || [],
    },
  };
}

export { listStacks }; 