import { React, useEffect, useState } from "react";
import "antd/dist/antd.css";
import axiosInstance from "../../config/axiosConfig";
import "./Home.css";
import { withRouter, useLocation } from "react-router-dom";
import { Menu } from "antd";
import {
  ORGANIZATION_ARCHIVE,
  ORGANIZATION_NAME,
} from "../../config/actionTypes";
import {
  CloudOutlined,
  SettingOutlined,
  AppstoreOutlined,
  DownCircleOutlined,
  PlusCircleOutlined,
} from "@ant-design/icons";

const { SubMenu } = Menu;

export const RegistryMenu = (props) => {
  const [orgs, setOrgs] = useState([]);
  const [menuHidden, setMenuHidden] = useState(false);
  const [defaultSelected, setDefaultSelected] = useState(["registry"]);
  const { organizationName, setOrganizationName, history } = props;
  const location = useLocation();
  const organizationId = localStorage.getItem(ORGANIZATION_ARCHIVE);
  useEffect(() => {
    axiosInstance.get("organization").then((response) => {
      setOrgs(prepareOrgs(response.data));
      setOrganizationName(
        localStorage.getItem(ORGANIZATION_NAME) || "select organization"
      );
    });
    if (location.pathname.includes("registry")) {
      setDefaultSelected(["registry"]);
    } else if (location.pathname.includes("settings")) {
      setDefaultSelected(["settings"]);
    } else {
      setDefaultSelected(["workspaces"]);
    }

    if (location.pathname.includes("portal")) {
      setMenuHidden(true);
    }
  }, [organizationId]);

  const handleClick = (e) => {
    if (e.key === "new") history.push("/organizations/create");
    else {
      history.push(`/organizations/${e.key}/workspaces`);
      setDefaultSelected(["workspaces"]);
      history.go(0);
    }
  };

  const handleRegistry = (e) => {
    history.push(`/organizations/${organizationId}/registry`);
    setDefaultSelected(["registry"]);
  };

  const handleWorkspaces = (e) => {
    history.push(`/organizations/${organizationId}/workspaces`);
    setDefaultSelected(["workspaces"]);
  };

  const handleSettings = (e) => {
    history.push(`/organizations/${organizationId}/settings`);
    setDefaultSelected(["settings"]);
  };

  return (
    <>
      <Menu
        hidden={menuHidden}
        selectedKeys={defaultSelected}
        theme="dark"
        mode="horizontal"
      >
        <SubMenu
          key="organization-name"
          icon={<DownCircleOutlined />}
          title={organizationName}
        >
          <Menu.Item
            icon={<PlusCircleOutlined />}
            onClick={handleClick}
            key="new"
          >
            Create new organization
          </Menu.Item>
          <Menu.Divider></Menu.Divider>
          <Menu.ItemGroup title="Organizations">
            {orgs
              .sort((a, b) => a.name.localeCompare(b.name))
              .map((org, index) => (
                <Menu.Item onClick={handleClick} key={org.id}>
                  {org.name}
                </Menu.Item>
              ))}
          </Menu.ItemGroup>
        </SubMenu>
        {organizationId !== null && (
          <>
            <Menu.Item
              key="workspaces"
              icon={<AppstoreOutlined />}
              onClick={handleWorkspaces}
            >
              Workspaces
            </Menu.Item>
            <Menu.Item
              key="registry"
              icon={<CloudOutlined />}
              onClick={handleRegistry}
            >
              Registry
            </Menu.Item>
            <Menu.Item
              key="settings"
              icon={<SettingOutlined />}
              onClick={handleSettings}
            >
              Settings
            </Menu.Item>{" "}
          </>
        )}
      </Menu>
    </>
  );
};

function prepareOrgs(organizations) {
  let orgs = [];
  organizations.data.forEach((element) => {
    orgs.push({
      id: element.id,
      name: element.attributes.name,
      description: element.attributes.description,
    });
  });

  return orgs;
}

export default withRouter(RegistryMenu);
