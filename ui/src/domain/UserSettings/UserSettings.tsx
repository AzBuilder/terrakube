import { Breadcrumb, Layout, Tabs } from "antd";
import { Tokens } from "./Tokens";
import "./UserSettings.css";
const { Content } = Layout;

export const UserSettings = () => {
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
