import { UserManager, WebStorageStateStore } from "oidc-client-ts";
import { AuthContext, AuthContextProps } from "react-oidc-context";
import React from "react";

export const oidcConfig = {
  authority: window._env_.REACT_APP_AUTHORITY,
  client_id: window._env_.REACT_APP_CLIENT_ID,
  redirect_uri: window._env_.REACT_APP_REDIRECT_URI,
  scope: window._env_.REACT_APP_SCOPE,
  userStore: new WebStorageStateStore({ store: window.localStorage }),
};

export const mgr = new UserManager(oidcConfig);

export const useAuth = (): AuthContextProps => {
  const context = React.useContext(AuthContext);

  if (!context) {
    throw new Error(
      "AuthProvider context is undefined, please verify you are calling useAuth() as child of a <AuthProvider> component."
    );
  }

  return context;
};
