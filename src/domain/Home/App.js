import { React } from 'react';
import './App.css';
import Login from '../Login/Login'
import { useIsAuthenticated } from "@azure/msal-react";
import { 
  BrowserRouter as Router,
  Switch,
  Route
} from 'react-router-dom';
import { Organizations } from '../Organizations';

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
      <Router>
        <Switch>
          <Route exact path="/">
            Cloud Builder Home.
          </Route>
          <Route path="/organizations">
            <Organizations />
          </Route>
        </Switch>
      </Router>
    </div>
  )
}

export default App;
