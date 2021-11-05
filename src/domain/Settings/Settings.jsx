import { React } from "react";

import {  Layout, Breadcrumb, Tabs} from "antd";
import './Settings.css';
import { GeneralSettings } from "./General";
import { TeamSettings } from "./Teams"
import { VCSSettings } from "./VCS";
import { TemplatesSettings } from "./Templates";
import {ORGANIZATION_NAME} from '../../config/actionTypes';
const { Content } = Layout;
const { TabPane } = Tabs;



export const OrganizationSettings = ({ selectedTab ,vcsMode}) => {
  return (
    <Content style={{ padding: '0 50px' }}>
      <Breadcrumb style={{ margin: '16px 0' }}>
        <Breadcrumb.Item>{localStorage.getItem(ORGANIZATION_NAME)}</Breadcrumb.Item>
        <Breadcrumb.Item>Settings</Breadcrumb.Item>
      </Breadcrumb>
      <div className="site-layout-content">
        <Tabs tabPosition="left" defaultActiveKey={selectedTab}>
          <TabPane tab="General" key="1">
            <GeneralSettings />
          </TabPane>
          <TabPane tab="Teams" key="2">
            <TeamSettings />
          </TabPane>
          <TabPane tab="VCS Providers" key="3">
            <VCSSettings vcsMode={vcsMode} />
          </TabPane>
          <TabPane tab="Templates" key="4">
            <TemplatesSettings />
          </TabPane>
        </Tabs>
      </div>
    </Content>
  );
}