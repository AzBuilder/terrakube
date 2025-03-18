import { ORGANIZATION_ARCHIVE } from "../../config/actionTypes";

const INITIAL_STATE = {};

const applyArchiveOrganization = (state: any, action: any) => {
  return { ...state, organizationId: action.resourceId };
};

function organizationReducer(state = INITIAL_STATE, action: any) {
  switch (action.type) {
    case ORGANIZATION_ARCHIVE:
      return applyArchiveOrganization(state, action);
    default:
      return state;
  }
}

export default organizationReducer;
