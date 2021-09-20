import { React } from 'react';
import 'antd/dist/antd.css';
import "./Home.css"
import { Layout, Breadcrumb } from 'antd';

const {  Content } = Layout;



export const Home = () => {
  return(
   
    <Content style={{ padding: '0 50px' }}>
      <Breadcrumb style={{ margin: '16px 0' }}>
        <Breadcrumb.Item>devops-organization</Breadcrumb.Item>
        <Breadcrumb.Item>Workspaces</Breadcrumb.Item>
      </Breadcrumb>
      <div className="site-layout-content">
        
      </div>
    </Content>
  
  )
}


