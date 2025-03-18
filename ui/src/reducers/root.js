import { combineReducers } from "redux";
import organizationReducer from "./organization/index";

const rootReducer = combineReducers({
  organizationState: organizationReducer,
});

export default rootReducer;
