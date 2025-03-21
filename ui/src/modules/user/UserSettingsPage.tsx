import { Breadcrumb, Layout, Tabs } from "antd";
import { Tokens } from "./components/PatSection/PatSection";
const { Content } = Layout;

export const UserSettingsPage = () => {
  return (
    <Content className="user-settings-page">
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
      <div className="tabs">
        <Tabs
          tabPosition="left"
          items={[
            {
              label: "Tokens (PAT)",
              key: "1",
              children: <Tokens />,
            },
          ]}
        />
      </div>
    </Content>
  );
};
