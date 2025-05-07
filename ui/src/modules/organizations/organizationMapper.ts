import { ApiWorkspaceTag, Organization } from "../../domain/types";
import { OrganizationModel, TagModel } from "./types";

function mapOrganization(apiData: Organization): OrganizationModel {
  return {
    id: apiData.id,
    name: apiData.attributes.name,
    description: apiData.attributes.description,
    executionMode: apiData.attributes.executionMode,
    icon: apiData.attributes.icon,
  };
}

function mapTag(apiData: ApiWorkspaceTag): TagModel {
  return {
    id: apiData.id,
    name: apiData.attributes.name,
  };
}

export { mapOrganization, mapTag };
