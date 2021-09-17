import { React,useState } from 'react';
import './App.css';
import Login from '../Login/Login'
import MainMenu from '../Home/MainMenu'
import { Layout, Avatar } from 'antd';
import { useIsAuthenticated } from "@azure/msal-react";

import {
  BrowserRouter as Router,
  Switch,
  Route
} from 'react-router-dom';
import { OrganizationDetails } from '../Organizations/Details';
import { CreateOrganization } from '../Organizations/Create';
import { Home } from './Home';
import { WorkspaceDetails } from '../Workspaces/Details';
import { CreateVariable } from '../Variables/Create';
import { CreateWorkspace } from '../Workspaces/Create';
import { CreateModule } from '../Modules/Create';
import { ModuleDetails } from '../Modules/Details';
const { Header, Footer } = Layout;

const App = () => {
  const isAuthenticated = useIsAuthenticated();
  const [organizationName, setOrganizationName] = useState([]);
  const [current, setCurrent] = useState('mail');
  
  
  if (!isAuthenticated) {
    return (
       <Login />
    )
  }
  const handleClick = e => {
    console.log('click ', e);
    setCurrent(e.key);
  };
  return (
    <Router>
      <Layout className="layout">
        <Header>
          <div className="logo" />
          
          <div className="menu">
            <MainMenu organizationName={organizationName} setOrganizationName={setOrganizationName}/>
          </div>
          <div className="user" >
            <Avatar shape="square" size="default" src="https://avatarfiles.alphacoders.com/128/thumb-128984.png" />
          </div>
        </Header>
        
        <Switch>
          <Route exact path="/" component={Home} />
          <Route exact path="/organizations/create" component={CreateOrganization} />
          <Route exact path="/organizations/:id">
             <OrganizationDetails setOrganizationName={setOrganizationName} organizationName={organizationName} />
          </Route>
          <Route exact path="/workspaces/create" component={CreateWorkspace} />
          <Route exact path="/workspaces/:name/variable/:create" component={CreateVariable} />
          <Route exact path="/workspaces/:id" component={WorkspaceDetails} />
          
          <Route exact path="/organizations/:orgid/registry/create" component={CreateModule} />
          <Route exact path="/organizations/:orgid/registry">
            <ModuleDetails setOrganizationName={setOrganizationName} organizationName={organizationName} />
          </Route>
        </Switch>
        <Footer style={{ textAlign: 'center' }}>AZBuilder ©2021</Footer>
      </Layout>
    </Router>
  )
}

export default App;
