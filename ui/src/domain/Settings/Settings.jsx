import { React, useState } from "react";
import { Layout, Breadcrumb, Tabs } from "antd";
import "./Settings.css";
import { GeneralSettings } from "./General";
import { TeamSettings } from "./Teams";
import { VCSSettings } from "./VCS";
import { TemplatesSettings } from "./Templates";
import { TagsSettings } from "./Tags";
import { ORGANIZATION_NAME } from "../../config/actionTypes";
import { SSHKeysSettings } from "./SSHKeys";
import { GlobalVariablesSettings } from "./GlobalVariables";
const { Content } = Layout;
const { TabPane } = Tabs;

export const OrganizationSettings = ({ selectedTab, vcsMode }) => {
  const [key, setKey] = useState("");
  function callback(key) {
    setKey(key);
  }
  return (
    <Content style={{ padding: "0 50px" }}>
      <Breadcrumb
        style={{ margin: "16px 0" }}
        items={[
          {
            title: localStorage.getItem(ORGANIZATION_NAME),
          },
          {
            title: "Settings",
          },
        ]}
      />

      <div className="site-layout-content">
        <Tabs
          tabPosition="left"
          defaultActiveKey={selectedTab}
          onTabClick={callback}
          items={[
            {
              label: "General",
              key: "1",
              children: <GeneralSettings />,
            },
            {
              label: "Teams",
              key: "2",
              children: <TeamSettings key={key} />,
            },
            {
              label: "Global Variables",
              key: "3",
              children: <GlobalVariablesSettings />,
            },
            {
              label: "VCS Providers",
              key: "4",
              children: <VCSSettings vcsMode={vcsMode} />,
            },
            {
              label: "Templates",
              key: "5",
              children: <TemplatesSettings key={key} />,
            },
            {
              label: "SSH Keys",
              key: "6",
              children: <SSHKeysSettings />,
            },
            {
              label: "Tags",
              key: "7",
              children: <TagsSettings />,
            },
          ]}
        />
      </div>
    </Content>
  );
};