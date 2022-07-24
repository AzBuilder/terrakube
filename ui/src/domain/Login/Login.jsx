import React from 'react';
import { MicrosoftLoginButton } from 'react-social-login-buttons';
import './Login.css';
import { Card ,Space} from "antd";
import logo from './logo.jpg';
import { mgr } from "../../config/authConfig";
const Login = () => {

  return (
    <div className='login-container'>
      <div className='login-wrapper'>

        <Card title={<img alt="logo"  className="loginLogo" src={logo} />}>
          <Space direction="vertical">
          Sign in to Terrakube
          <MicrosoftLoginButton onClick={() => App()} />
          </Space>
        </Card>

      </div> </div>
  )
}

function App() {
  console.log(mgr);
  mgr.signinRedirect();
}

export default Login;