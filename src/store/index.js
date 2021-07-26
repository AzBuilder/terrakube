import { createStore } from "redux";
import userReducer from "../reducers/user";

const store = createStore(
  userReducer
);

export default store;