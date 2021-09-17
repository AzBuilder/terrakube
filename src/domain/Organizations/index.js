import { React, useEffect, useState } from "react";
import { useHistory } from 'react-router-dom';
import axiosInstance from "../../config/axiosConfig";
import { Menu } from "antd";
import "./Organizations.css";
import { DownCircleOutlined, PlusCircleOutlined, SelectOutlined } from '@ant-design/icons';
import { ORGANIZATION_NAME } from '../../config/actionTypes';
const { SubMenu } = Menu;



export const Organizations = ({ organizationName, setOrganizationName }) => {
  const [orgs, setOrgs] = useState([]);
  const history = useHistory();

  useEffect(() => {
    axiosInstance.get("organization")
      .then(response => {
        setOrgs(prepareOrgs(response.data));
        setOrganizationName(localStorage.getItem(ORGANIZATION_NAME) || "select organization");
      })
  }, []);

  const handleClick = e => {
    if (e.key == "new")
      history.push("/organizations/create")
    else
      history.push("/organizations/" + e.key)

  };

  return (
    <Menu  theme="dark" mode="horizontal" >
      <SubMenu key="organization-name" icon={<DownCircleOutlined />} title={organizationName}>
        <Menu.Item icon={<PlusCircleOutlined />} onClick={handleClick} key="new" >Create new organization</Menu.Item>
        <Menu.Divider></Menu.Divider>
        <Menu.ItemGroup title="Organizations">
          {orgs.sort((a, b) => a.name.localeCompare(b.name))
            .map((org, index) => (
              <Menu.Item onClick={handleClick} key={org.id} >{org.name}</Menu.Item>
            ))}
        </Menu.ItemGroup>
      </SubMenu>
    </Menu>
  );
};


function prepareOrgs(organizations) {
  let orgs = []
  organizations.data.forEach(element => {
    orgs.push({
      id: element.id,
      name: element.attributes.name,
      description: element.attributes.description
    });
  });

  return orgs;
}

