import React from 'react';
import './App.css';
import Login from '../signIn/Login'
import { useIsAuthenticated } from "@azure/msal-react";

const App = () => {
  const isAuthenticated = useIsAuthenticated();

  if(!isAuthenticated) {
    return (
      <div className='App'>
        <Login />
      </div>
    )
  }

  return(
    <div className="App">
      Cloud Builder Home
    </div>
  )
}

export default App;
