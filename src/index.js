import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './domain/Home/App';
import reportWebVitals from './reportWebVitals';

import { MsalProvider } from "@azure/msal-react";
import {msalInstance } from "./config/authConfig";



ReactDOM.render(
  <React.StrictMode>
    <MsalProvider instance={msalInstance}>
      <App />
    </MsalProvider>
  </React.StrictMode>,
  document.getElementById('root')
);

reportWebVitals();