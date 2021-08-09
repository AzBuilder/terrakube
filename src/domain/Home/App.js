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
import { CreateOrganization } from '../Organizations/Create';
import { Home } from './Home';
import { WorkspaceDetails } from '../Workspaces/Details';

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
          <Route exact path="/" component={Home} />
          <Route exact path="/organizations" component={Organizations} />
          <Route exact path="/organizations/create" component={CreateOrganization} />
          <Route exact path="/organizations/:id" component={OrganizationDetails} />

          <Route exact path="/workspaces/:id" component={WorkspaceDetails} />
        </Switch>
      </Router>
    </div>
  )
}

export default App;
