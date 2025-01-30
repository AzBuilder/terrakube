import React from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import App from './domain/Home/App';
import reportWebVitals from './reportWebVitals';
import { AuthProvider } from "react-oidc-context";
import { oidcConfig } from './config/authConfig';
import "github-markdown-css/github-markdown.css";

// Get the root element from the DOM
const container = document.getElementById('root');

// Create a root
const root = createRoot(container);

// Initial render
root.render(
  <React.StrictMode>
    <AuthProvider {...oidcConfig}>
      <App />
    </AuthProvider>
  </React.StrictMode>
);

reportWebVitals();