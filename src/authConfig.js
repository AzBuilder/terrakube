export const msalConfig = {
  auth: {
    clientId: "853b26d6-1849-4c00-8543-da5805b0e593",
    authority: "https://login.microsoftonline.com/0e6427af-ab9e-4af6-9f6f-bc098f470d75",
    redirectUri: "http://localhost:3000",
  },
  cache: {
    cacheLocation: "sessionStorage", // This configures where your cache will be stored
    storeAuthStateInCookie: false, // Set this to "true" if you are having issues on IE11 or Edge
  }
};

// Add scopes here for ID token to be used at Microsoft identity platform endpoints.
export const loginRequest = {
 scopes: ["openid", "profile", "User.Read"]
};

// Add scopes here for access token to be used at Microsoft Graph API endpoints.
const tokenRequest = {
  scopes: ["User.Read", "Mail.Read"]
};

// Add the endpoints here for Microsoft Graph API services you'd like to use.
const graphConfig = {
  graphMeEndpoint: "https://graph.microsoft.com/v1.0/me",
  graphMailEndpoint: "https://graph.microsoft.com/v1.0/me/messages"
};