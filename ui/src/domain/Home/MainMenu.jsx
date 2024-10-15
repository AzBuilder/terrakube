import { React, useEffect, useState } from "react";
import "antd/dist/reset.css";
import axiosInstance from "../../config/axiosConfig";
import "./Home.css";
import { useLocation, useNavigate } from "react-router-dom";
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

export const RegistryMenu = (props) => {
  const [orgs, setOrgs] = useState([]);
  const [defaultSelected, setDefaultSelected] = useState(["registry"]);
  const { organizationName, setOrganizationName } = props;
  const location = useLocation();
  const navigate = useNavigate();
  const organizationId = sessionStorage.getItem(ORGANIZATION_ARCHIVE);

  useEffect(() => {
    axiosInstance.get("organization").then((response) => {
      setOrgs(prepareOrgs(response.data));
      setOrganizationName(
        sessionStorage.getItem(ORGANIZATION_NAME) || "select organization"
      );
    });

    if (location.pathname.includes("registry")) {
      setDefaultSelected(["registry"]);
    } else if (location.pathname.includes("settings")) {
      setDefaultSelected(["settings"]);
    } else {
      setDefaultSelected(["workspaces"]);
    }
  }, [organizationId, location.pathname, setOrganizationName]);

  const handleClick = (e) => {
    if (e.key === "new") navigate("/organizations/create");
    else {
      navigate(`/organizations/${e.key}/workspaces`);
      setDefaultSelected(["workspaces"]);
      navigate(0);
    }
  };

  const items = [
    {
      label: organizationName,
      key: "organization-name",
      icon: <DownCircleOutlined />,
      children: [
        {
          label: "Create new organization",
          key: "new",
          icon: <PlusCircleOutlined />,
          onClick: handleClick,
        },
        {
          type: "divider",
        },
        {
          type: 'group',
          label: "Organizations",
          children: orgs.sort((a, b) => a.name.localeCompare(b.name)).map((org) => ({
            label: org.name,
            key: org.id,
            onClick: handleClick,
          })),
        },
      ],
    },
    organizationId && {
      label: "Workspaces",
      key: "workspaces",
      icon: <AppstoreOutlined />,
      onClick: () => {
        navigate(`/organizations/${organizationId}/workspaces`);
        setDefaultSelected(["workspaces"]);
      },
    },
    organizationId && {
      label: "Registry",
      key: "registry",
      icon: <CloudOutlined />,
      onClick: () => {
        navigate(`/organizations/${organizationId}/registry`);
        setDefaultSelected(["registry"]);
      },
    },
    organizationId && {
      label: "Settings",
      key: "settings",
      icon: <SettingOutlined />,
      onClick: () => {
        navigate(`/organizations/${organizationId}/settings`);
        setDefaultSelected(["settings"]);
      },
    },
  ].filter(Boolean); // Filters out falsey values if organizationId is null

  return (
    <>
      <Menu
        selectedKeys={defaultSelected}
        theme="dark"
        mode="horizontal"
        items={items}
      />
    </>
  );
};

function prepareOrgs(organizations) {
  return organizations.data.map((element) => ({
    id: element.id,
    name: element.attributes.name,
    description: element.attributes.description,
  }));
}

export default RegistryMenu;