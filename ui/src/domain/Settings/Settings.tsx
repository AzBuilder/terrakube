import { Breadcrumb, Layout, Tabs, Tag } from "antd";
import { useState } from "react";
import { ORGANIZATION_NAME } from "../../config/actionTypes";
import { ActionSettings } from "./Actions";
import { GeneralSettings } from "./General";
import { GlobalVariablesSettings } from "./GlobalVariables";
import "./Settings.css";
import { SSHKeysSettings } from "./SSHKeys";
import { TagsSettings } from "./Tags";
import { TeamSettings } from "./Teams";
import { TemplatesSettings } from "./Templates";
import { VCSSettings } from "./VCS";
const { Content } = Layout;

type Props = {
  selectedTab?: string;
  vcsMode?: "new" | "list";
};

export const OrganizationSettings = ({ selectedTab, vcsMode }: Props) => {
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
            title: sessionStorage.getItem(ORGANIZATION_NAME),
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
            {
              label: (
                <>
                  Actions <Tag color="blue">beta</Tag>
                </>
              ),
              key: "8",
              children: <ActionSettings />,
            },
          ]}
        />
      </div>
    </Content>
  );
};
