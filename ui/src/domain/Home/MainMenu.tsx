import {
  AppstoreOutlined,
  CloudOutlined,
  DownCircleOutlined,
  PlusCircleOutlined,
  SettingOutlined,
} from "@ant-design/icons";
import { Menu } from "antd";
import "antd/dist/reset.css";
import { AxiosResponse } from "axios";
import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { ORGANIZATION_ARCHIVE, ORGANIZATION_NAME } from "../../config/actionTypes";
import axiosInstance from "../../config/axiosConfig";
import { ApiResponse, FlatOrganization, Organization } from "../types";
import "./Home.css";

type Props = {
  organizationName: string;
  setOrganizationName: React.Dispatch<React.SetStateAction<string>>;
};

export const MainMenu = ({ organizationName, setOrganizationName }: Props) => {
  const [orgs, setOrgs] = useState<FlatOrganization[]>([]);
  const [defaultSelected, setDefaultSelected] = useState(["registry"]);
  const location = useLocation();
  const navigate = useNavigate();
  const organizationId = sessionStorage.getItem(ORGANIZATION_ARCHIVE);

  useEffect(() => {
    axiosInstance.get("organization").then((response: AxiosResponse<ApiResponse<Organization[]>>) => {
      setOrgs(prepareOrgs(response.data.data));
      setOrganizationName(sessionStorage.getItem(ORGANIZATION_NAME) || "select organization");
    });

    if (location.pathname.includes("registry")) {
      setDefaultSelected(["registry"]);
    } else if (location.pathname.includes("settings")) {
      setDefaultSelected(["settings"]);
    } else {
      setDefaultSelected(["workspaces"]);
    }
  }, [organizationId, location.pathname, setOrganizationName]);

  const handleClick = (e: { key: string }) => {
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
          type: "group",
          label: "Organizations",
          children: orgs
            .sort((a, b) => a.name.localeCompare(b.name))
            .map((org) => ({
              label: org.name,
              key: org.id,
              onClick: handleClick,
            })),
        },
      ],
    },

    ...(organizationId
      ? [
          {
            label: "Workspaces",
            key: "workspaces",
            icon: <AppstoreOutlined />,
            onClick: () => {
              navigate(`/organizations/${organizationId}/workspaces`);
              setDefaultSelected(["workspaces"]);
            },
          },
          {
            label: "Registry",
            key: "registry",
            icon: <CloudOutlined />,
            onClick: () => {
              navigate(`/organizations/${organizationId}/registry`);
              setDefaultSelected(["registry"]);
            },
          },
          {
            label: "Settings",
            key: "settings",
            icon: <SettingOutlined />,
            onClick: () => {
              navigate(`/organizations/${organizationId}/settings`);
              setDefaultSelected(["settings"]);
            },
          },
        ]
      : []),
  ];

  return (
    <>
      <Menu selectedKeys={defaultSelected} theme="dark" mode="horizontal" items={items} />
    </>
  );
};

function prepareOrgs(organizations: Organization[]): FlatOrganization[] {
  return organizations.map((element) => ({
    id: element.id,
    name: element.attributes.name,
    description: element.attributes.description,
  }));
}

export default MainMenu;
