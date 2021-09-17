import { React ,useEffect,useState} from 'react';
import 'antd/dist/antd.css';
import axiosInstance from "../../config/axiosConfig";
import "./Home.css"
import { withRouter } from 'react-router-dom';
import { Menu } from 'antd';
import { ORGANIZATION_ARCHIVE,ORGANIZATION_NAME } from '../../config/actionTypes';
import { CloudOutlined, SettingOutlined, AppstoreOutlined,DownCircleOutlined, PlusCircleOutlined } from '@ant-design/icons';

const { SubMenu } = Menu;

export const RegistryMenu = (props) => {
  const [orgs, setOrgs] = useState([]);
  const { organizationName,setOrganizationName, history } = props;
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
  
  const handleRegistry = e => {
    const organizationId = localStorage.getItem(ORGANIZATION_ARCHIVE)
    history.push("/organizations/" + organizationId + "/registry");
  };

  const handleWorkspaces = e => {
    const organizationId = localStorage.getItem(ORGANIZATION_ARCHIVE)
    history.push("/organizations/" + organizationId);
  };

  return (
    <>
      <Menu theme="dark" mode="horizontal" >
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
        <Menu.Item key="1" icon={<AppstoreOutlined />} onClick={handleWorkspaces}>
          Workspaces
        </Menu.Item>
        <Menu.Item key="registry" icon={<CloudOutlined />} onClick={handleRegistry}>
          Registry
        </Menu.Item>
        <Menu.Item key="settings" icon={<SettingOutlined />}>
          Settings
        </Menu.Item>
      </Menu>
    </>
  )
}

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

export default withRouter(RegistryMenu)