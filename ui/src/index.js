import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './domain/Home/App';
import reportWebVitals from './reportWebVitals';
import { AuthProvider } from "react-oidc-context";
import { oidcConfig } from './config/authConfig'

ReactDOM.render(
  <React.StrictMode>
   <AuthProvider {...oidcConfig}>
      <App />
    </AuthProvider>,
  </React.StrictMode>,
  document.getElementById('root')
);

reportWebVitals();