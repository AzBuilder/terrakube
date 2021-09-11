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
            <Avatar shape="square" size="default" src="https://scontent.fsal5-1.fna.fbcdn.net/v/t1.6435-9/64248240_435198120394124_7423615203201253376_n.png?_nc_cat=109&ccb=1-5&_nc_sid=174925&_nc_ohc=Z__Q0f4a_WIAX8KxCZE&_nc_ht=scontent.fsal5-1.fna&oh=3f979cc60a562b48418a00e22849ec50&oe=6162C513" />
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
