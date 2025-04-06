import { Breadcrumb, Layout, Tabs, theme } from "antd";
import { useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { Tokens } from "./components/PatSection/PatSection";
import { ThemeSection } from "./components/ThemeSection/ThemeSection";
import "./UserSettingsPage.css";
const { Content } = Layout;

const tabs = {
  tokens: "1",
  theme: "2",
};

export const UserSettingsPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const currentTab = location.pathname.includes("/settings/theme") ? "2" : "1";
  const {
    token: { colorBgContainer },
  } = theme.useToken();

  useEffect(() => {
    // Update URL when component mounts to match the current tab
    if (currentTab === "2" && !location.pathname.includes("/settings/theme")) {
      navigate("/settings/theme", { replace: true });
    } else if (currentTab === "1" && !location.pathname.includes("/settings/tokens")) {
      navigate("/settings/tokens", { replace: true });
    }
  }, []);

  const handleTabChange = (key: string) => {
    if (key === "2") {
      navigate("/settings/theme");
    } else {
      navigate("/settings/tokens");
    }
  };

  return (
    <Content className="user-settings-page">
      <Breadcrumb
        style={{ margin: "16px 0" }}
        items={[
          {
            title: "Settings",
          },
          {
            title: currentTab === "2" ? "Theme" : "Tokens",
          },
        ]}
      />
      <div className="tabs" style={{ background: colorBgContainer }}>
        <Tabs
          tabPosition="left"
          activeKey={currentTab}
          onChange={handleTabChange}
          items={[
            {
              label: "Tokens (PAT)",
              key: "1",
              children: <Tokens />,
            },
            {
              label: "Theme",
              key: "2",
              children: <ThemeSection />,
            },
          ]}
        />
      </div>
    </Content>
  );
};
