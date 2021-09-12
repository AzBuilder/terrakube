import { React } from 'react';
import './App.css';
import Login from '../Login/Login'
import { Layout, Menu, Avatar } from 'antd';
import { AppstoreOutlined, SettingOutlined, CloudOutlined } from '@ant-design/icons';
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
import { CreateJob } from '../Jobs/Create';
import { CreateVariable } from '../Variables/Create';
import { CreateWorkspace } from '../Workspaces/Create';
import { CreateModule } from '../Modules/Create';
const { Header, Footer } = Layout;

const App = () => {
  const isAuthenticated = useIsAuthenticated();

  if (!isAuthenticated) {
    return (
      <div className='App'>
        <Login />
      </div>
    )
  }

  return (
    <Router>
      <Layout className="layout">
        <Header>
          <div className="logo" />
          <div className="menu">
            <Menu theme="dark" mode="horizontal" defaultSelectedKeys={['workspaces']}>
              <Organizations></Organizations>
              <Menu.Item key="workspaces" icon={<AppstoreOutlined />}>
                Workspaces
              </Menu.Item>
              <Menu.Item key="registry" icon={<CloudOutlined />}>
                Registry
              </Menu.Item>
              <Menu.Item key="settings" icon={<SettingOutlined />}>
                Settings
              </Menu.Item>
            </Menu>
          </div>
          <div className="user" >
            <Avatar shape="square" size="default" src="https://avatarfiles.alphacoders.com/128/thumb-128984.png" />
          </div>
        </Header>
        
        <Switch>
          <Route exact path="/" component={Home} />
          <Route exact path="/organizations/create" component={CreateOrganization} />
          <Route exact path="/organizations/:id" component={OrganizationDetails} />

          <Route exact path="/workspaces/create" component={CreateWorkspace} />
          <Route exact path="/workspaces/:name/jobs/:create" component={CreateJob} />
          <Route exact path="/workspaces/:name/variable/:create" component={CreateVariable} />
          <Route exact path="/workspaces/:id" component={WorkspaceDetails} />

          <Route exact path="/modules/create" component={CreateModule} />
        </Switch>
        <Footer style={{ textAlign: 'center' }}>AZBuilder ©2021</Footer>
      </Layout>
    </Router>
  )
}

export default App;
