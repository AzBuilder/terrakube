import { ORGANIZATION_ARCHIVE } from "../../config/actionTypes";

const INITIAL_STATE = {};

const applyArchiveOrganization = (state, action) => {
  return { ...state, organizationId: action.resourceId };
};

function organizationReducer(state = INITIAL_STATE, action) {
  switch (action.type) {
    case ORGANIZATION_ARCHIVE:
      return applyArchiveOrganization(state, action);
    default:
      return state;
  }
}

export default organizationReducer;
