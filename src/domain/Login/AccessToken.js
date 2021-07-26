import { React, useState } from 'react';
import { useMsal } from "@azure/msal-react";
import { loginRequest } from "../../config/authConfig";
import axios from 'axios';

function GetAccessToken() {
  const { instance, accounts, inProgress } = useMsal();
  const [accessToken, setAccessToken] = useState(null);

  const name = accounts[0] && accounts[0].name;

  const request = {
    ...loginRequest,
    account: accounts[0]
  };

  // Silently acquires an access token which is then attached to a request for Microsoft Graph data
  instance.acquireTokenSilent(request).then((response) => {
    //setAccessToken(response.accessToken);
    axios.defaults.headers.common['Authorization'] = response.accessToken;
    console.log(response.accessToken);
  }).catch((e) => {
    instance.acquireTokenPopup(request).then((response) => {
      console.log(response.accessToken);
    });
  });
};

export default GetAccessToken;
