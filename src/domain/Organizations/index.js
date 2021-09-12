import { React, useEffect, useState } from "react";
import { useHistory } from 'react-router-dom';
import axiosInstance from "../../config/axiosConfig";
import { Menu } from "antd";
import "./Organizations.css";
import { DownCircleOutlined,PlusCircleOutlined, SelectOutlined} from '@ant-design/icons';
const { SubMenu } = Menu;



export const Organizations = () => {
  const [orgs, setOrgs] = useState([]);

  const history = useHistory();

  useEffect(() => {
    axiosInstance.get("organization")
      .then(response => {
        setOrgs(prepareOrgs(response.data));
      })
  }, []);

  const handleClick = e => {
    if (e.key =="new")
      history.push("/organizations/create")
    else
      history.push("/organizations/"+e.key)
  };

  return (
    <SubMenu key="organization-name" icon={<DownCircleOutlined />} title="organization-name">
      <Menu.Item icon={<PlusCircleOutlined />} onClick={handleClick}  key="new" >Create new organization</Menu.Item>
      <Menu.Divider></Menu.Divider>
      <Menu.ItemGroup  title="Organizations">
      {orgs.sort((a, b) => a.name.localeCompare(b.name))
        .map((org, index) => (
          <Menu.Item onClick={handleClick}  key={org.id} >{org.name}</Menu.Item>
        ))}
     </Menu.ItemGroup>
    </SubMenu>
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

