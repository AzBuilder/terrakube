import { React, useState, useEffect } from 'react';
import { Button, Layout, Breadcrumb, Card, List, Space, Input, Tag } from "antd";
import axiosInstance from "../../config/axiosConfig";
import { useParams,useHistory,Link } from "react-router-dom";
import { CloudUploadOutlined,CloudOutlined, ClockCircleOutlined, DownloadOutlined } from '@ant-design/icons';
import { SiMicrosoftazure,SiAmazonaws } from "react-icons/si";
import { RiFolderHistoryLine } from "react-icons/ri";
import { IconContext } from "react-icons";
import { MdBusiness } from 'react-icons/md';
import { compareVersions } from '../Workspaces/Workspaces'
import './Module.css';
import { ORGANIZATION_ARCHIVE, ORGANIZATION_NAME } from '../../config/actionTypes';


const { Content } = Layout;
const include = { MODULE: "module" }
const { Search } = Input;


export const ModuleList = ({ setOrganizationName, organizationName }) => {
  const { orgid } = useParams();
  const [organization, setOrganization] = useState({});
  const [modules, setModules] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    localStorage.setItem(ORGANIZATION_ARCHIVE, orgid);
    axiosInstance.get(`organization/${orgid}?include=module`)
      .then(response => {
        console.log(response);
        setOrganization(response.data);

        if (response.data.included) {
          setupOrganizationIncludes(response.data.included, setModules);
        }

        setLoading(false);
        localStorage.setItem(ORGANIZATION_NAME, response.data.data.attributes.name)
        setOrganizationName(response.data.data.attributes.name)
      });

  }, [orgid]);
  const history = useHistory();
  const handleClick = id => {
    console.log(id);
    history.push("/organizations/"+orgid+"/registry/"+id)
  };

  const renderLogo = (provider) => {
    switch(provider) {
      case 'azurerm':
        return <IconContext.Provider value={{ color: "#008AD7", size: "1.5em" }}><SiMicrosoftazure /></IconContext.Provider>;
      case 'aws':
        return <IconContext.Provider value={{ color:"#232F3E", size: "1.5em" }}><SiAmazonaws/></IconContext.Provider>;
      default:
        return <CloudOutlined />;
    }
  }

  const handlePublish = () => {
    history.push("/organizations/"+orgid+"/registry/create")
  };

  return (
    <Content style={{ padding: '0 50px' }}>
      <Breadcrumb style={{ margin: '16px 0' }}>
        <Breadcrumb.Item>{organizationName}</Breadcrumb.Item>
        <Breadcrumb.Item><Link to={`/organizations/${orgid}/registry`}>Modules</Link></Breadcrumb.Item>
      </Breadcrumb>
      <div className="site-layout-content">
        {loading || !organization.data || !modules ? (
          <p>Data loading...</p>
        ) : (
          <div className="modulesWrapper">
            <div className='variableActions'><h2>Modules</h2><Button type="primary" htmlType="button" icon={<CloudUploadOutlined />} onClick={handlePublish}>Publish module</Button></div>
            <Search placeholder="Filter modules" style={{ width: "100%" }} />
            <List split="" className="moduleList" dataSource={modules}
              renderItem={item => (
                <List.Item>
                  <Card onClick={() => handleClick(item.id)} style={{ width: "100%" }} hoverable>
                    <Space style={{ color: "rgb(82, 87, 97)" }} direction="vertical" >
                      <h3>{item.name}</h3>
                      {item.description}
                      <Space size={40} style={{ marginTop: "25px" }}>
                        <Tag color="blue"><span><MdBusiness /> Private</span></Tag>
                        <span>{renderLogo(item.provider)}&nbsp;&nbsp;{item.provider}</span>
                        <span><IconContext.Provider value={{ size: "1.3em" }}><RiFolderHistoryLine /></IconContext.Provider>&nbsp;&nbsp;{item.versions.sort(compareVersions).reverse()[0]}</span>
                        <span><ClockCircleOutlined />&nbsp;&nbsp;1 minute ago</span>
                        <span><DownloadOutlined />&nbsp;&nbsp; &lt; 100</span>
                      </Space>
                    </Space>
                  </Card>
                </List.Item>
              )}
            />
          </div>
        )}
      </div>
    </Content>

  );
}

function setupOrganizationIncludes(includes, setModules, setWorkspaces) {
  let modules = [];

  includes.forEach(element => {
    switch (element.type) {
      case include.MODULE:
        modules.push(
          {
            id: element.id,
            ...element.attributes
          }
        );
        break;
      default:
        break;
    }
  });

  setModules(modules);
}