import { Breadcrumb, Layout, Tabs, Tag, theme } from "antd";
import { useState } from "react";
import { ORGANIZATION_NAME } from "../../config/actionTypes";
import { ActionSettings } from "./Actions";
import { GeneralSettings } from "./General";
import { GlobalVariablesSettings } from "./GlobalVariables";
import "./Settings.css";
import { SSHKeysSettings } from "./SSHKeys";
import { AgentSettings } from "./Agents";
import { TagsSettings } from "./Tags";
import { TeamSettings } from "./Teams";
import { TemplatesSettings } from "./Templates";
import { VCSSettings } from "./VCS";
import { useNavigate, useParams } from "react-router-dom";
const { Content } = Layout;

type Props = {
  selectedTab?: string;
  vcsMode?: "new" | "list";
};

export const OrganizationSettings = ({ selectedTab, vcsMode }: Props) => {
  const { orgid } = useParams();
  const navigate = useNavigate();
  const [key, setKey] = useState("");
  const { token } = theme.useToken();

  function callback(key: string) {
    setKey(key);
  }

  return (
    <Content style={{ padding: "0 50px", background: token.colorBgContainer }}>
      <Breadcrumb
        style={{ margin: "16px 0" }}
        items={[
          {
            title: sessionStorage.getItem(ORGANIZATION_NAME),
          },
          {
            title: "Settings",
          },
        ]}
      />

      <div className="site-layout-content" style={{ background: token.colorBgContainer }}>
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
            {
              label: "Agents",
              key: "8",
              children: <AgentSettings />,
            },
            {
              label: (
                <>
                  Actions <Tag color={token.colorPrimary}>beta</Tag>
                </>
              ),
              key: "9",
              children: <ActionSettings />,
            },
          ]}
        />
      </div>
    </Content>
  );
};
