import { React, useEffect, useState } from "react";
import "antd/dist/reset.css";
import "./Home.css";
import { Avatar, Dropdown, message } from "antd";
import {
  UserOutlined,
  PoweroffOutlined,
  QuestionCircleOutlined,
} from "@ant-design/icons";
import getUserFromStorage from "../../config/authUser";
import { useAuth } from "../../config/authConfig";
import { useNavigate } from "react-router-dom";
import {
  ORGANIZATION_ARCHIVE,
  ORGANIZATION_NAME,
} from "../../config/actionTypes";

export const ProfilePicture = (props) => {
  const [username, setUsername] = useState(null);
  const auth = useAuth();
  const navigate = useNavigate();

  const handleUserSettings = () => {
    navigate(`/settings/tokens`);
  };

  const signOutClickHandler = () => {
    auth.removeUser();
    sessionStorage.removeItem(ORGANIZATION_NAME);
    sessionStorage.removeItem(ORGANIZATION_ARCHIVE);
  };

  useEffect(() => {
    const user = getUserFromStorage();
    if (user && user.profile) {
      setUsername(user.profile.name);
    }
  }, []);

  const menuItems = [
    {
      key: "user-id",
      label: (
        <>
          Signed in as <br />
          <b>{username}</b>
        </>
      ),
      onClick: () => message.info("Coming Soon"),
    },
    {
      type: "divider",
    },
    {
      key: "user-settings",
      icon: <UserOutlined />,
      label: "User Settings",
      onClick: handleUserSettings,
    },
    {
      key: "help",
      icon: <QuestionCircleOutlined />,
      label: (
        <a
          target="_blank"
          rel="noopener noreferrer"
          href="https://docs.terrakube.io/"
        >
          Help
        </a>
      ),
    },
    {
      key: "sign-out",
      icon: <PoweroffOutlined />,
      label: "Sign Out",
      onClick: signOutClickHandler,
    },
  ];

  return (
    <>
      <Dropdown
        menu={{
          items: menuItems,
          theme: "dark",
        }}
        trigger={["click"]}
      >
        <Avatar
          className="avatar-hover-effect"
          size="default"
          icon={<UserOutlined />}
        />
      </Dropdown>
    </>
  );
};