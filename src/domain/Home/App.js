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
import { OrganizationDetails } from '../Organizations/Details';

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
          <Route exact path="/organizations" component={Organizations} />
          <Route exact path="/organizations/:id" component={OrganizationDetails} />
        </Switch>
      </Router>
    </div>
  )
}

export default App;
