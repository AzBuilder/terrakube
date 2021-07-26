import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './domain/Home/App';
import reportWebVitals from './reportWebVitals';
import { PageHeader } from 'antd';
import { PublicClientApplication } from "@azure/msal-browser";
import { MsalProvider } from "@azure/msal-react";
import { msalConfig } from "./config/authConfig";

const msalInstance = new PublicClientApplication(msalConfig);

ReactDOM.render(
  <React.StrictMode>
    <MsalProvider instance={msalInstance}>
      <div className="site-page-header"> 
        <PageHeader
          ghost={false}
          title='Cloud Builder'
          subTitle='Execute Terraform operations as if you were on Terraform Enterprise.'
        />
      </div>
      <App />
    </MsalProvider>
  </React.StrictMode>,
  document.getElementById('root')
);

reportWebVitals();