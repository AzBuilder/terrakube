import React from 'react';
import { MicrosoftLoginButton } from 'react-social-login-buttons';
import { useMsal } from "@azure/msal-react";
import { loginRequest } from "../../authConfig";

const Login = () => {
  const { instance } = useMsal();

  return (
    <div className='login-wrapper'>
      <MicrosoftLoginButton onClick={() => handleLogin(instance)} />
    </div>
  )
}

function handleLogin(instance) {
  instance.loginPopup(loginRequest).catch(e => {
    console.error(e);
  });
}

export default Login;