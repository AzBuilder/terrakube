import { React, useState } from "react";
import { Layout, Breadcrumb, Tabs } from "antd";
import "./UserSettings.css";
import { Tokens } from "./Tokens";
const { Content } = Layout;
const { TabPane } = Tabs;

export const UserSettings = () => {
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
            title: "Settings",
          },
          {
            title: "Tokens",
          },
        ]}
      />
      <div className="site-layout-content">
        <Tabs
          tabPosition="left"
          onTabClick={callback}
          items={[
            {
              label: "Tokens",
              key: "1",
              children: <Tokens />,
            },
          ]}
        />
      </div>
    </Content>
  );
};
