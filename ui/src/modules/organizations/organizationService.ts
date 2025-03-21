import { apiGet } from "@/modules/api/apiWrapper";
import { ApiResponse } from "@/modules/api/types";
import { Organization } from "../../domain/types";

async function listOrganizations(): Promise<ApiResponse<Organization[]>> {
  return await apiGet("/api/v1/organization", { dataWrapped: true });
}

const methods = {
  listOrganizations,
};

export default methods;
