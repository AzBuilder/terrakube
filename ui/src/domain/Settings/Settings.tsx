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
import { VariableCollectionsSettings } from "./VariableCollections";
import { CreateEditCollection } from "./CreateEditCollection";
import { useNavigate, useParams, useLocation } from "react-router-dom";
const { Content } = Layout;

type Props = {
  selectedTab?: string;
  vcsMode?: "new" | "list";
  collectionMode?: "list" | "new" | "edit" | "detail";
  collectionId?: string;
};

export const OrganizationSettings = ({ selectedTab, vcsMode, collectionMode = "list", collectionId }: Props) => {
  const { orgid } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const [key, setKey] = useState("");
  const { token } = theme.useToken();

  function callback(key: string) {
    setKey(key);

    // Special case for collection tab
    if (key === "9" && location.pathname.includes("/collection/")) {
      navigate(`/organizations/${orgid}/settings/collection`);
    }
  }

  // Render appropriate content for Variable Collections tab
  const renderCollectionContent = () => {
    switch (collectionMode) {
      case "new":
        return <CreateEditCollection mode="create" />;
      case "edit":
        return <CreateEditCollection mode="edit" collectionId={collectionId} />;
      case "list":
      default:
        return <VariableCollectionsSettings />;
    }
  };

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
              label: "Variable Collections",
              key: "9",
              children: renderCollectionContent(),
            },
            {
              label: (
                <>
                  Actions <Tag color={token.colorPrimary}>beta</Tag>
                </>
              ),
              key: "10",
              children: <ActionSettings />,
            },
          ]}
        />
      </div>
    </Content>
  );
};
