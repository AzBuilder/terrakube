export const msalConfig = {
  auth: {
    clientId: window._env_.REACT_APP_CLIENT_ID,
    authority: window._env_.REACT_APP_AUTHORITY,
    redirectUri: window._env_.REACT_APP_REDIRECT_URI,
  },
  cache: {
    cacheLocation: "sessionStorage", // This configures where your cache will be stored
    storeAuthStateInCookie: false, // Set this to "true" if you are having issues on IE11 or Edge
  }
};

// Add scopes here for ID token to be used at Microsoft identity platform endpoints.
export const loginRequest = {
 scopes: [window._env_.REACT_APP_SCOPE]
};