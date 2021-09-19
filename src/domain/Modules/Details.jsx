import { React, useState, useEffect } from 'react';
import { Menu,Button, Layout, Breadcrumb, Dropdown, Alert, Tabs, Space, Input, Tag, Row, Col, Card, Divider } from "antd";
import axiosInstance from "../../config/axiosConfig";
import { useParams,Link } from "react-router-dom";
import { DownOutlined,CloudOutlined , ClockCircleOutlined, DownloadOutlined } from '@ant-design/icons';
import { SiMicrosoftazure ,SiAmazonaws} from "react-icons/si";
import { BiBookBookmark } from "react-icons/bi";
import { RiFolderHistoryLine, RiGithubFill } from "react-icons/ri";
import { IconContext } from "react-icons";
import { MdBusiness } from 'react-icons/md';
import './Module.css';
import { ORGANIZATION_ARCHIVE, ORGANIZATION_NAME } from '../../config/actionTypes';

const { TabPane } = Tabs;
const { Content } = Layout;
const { DateTime } = require("luxon");
const include = {
  WORKSPACE: "workspace",
  MODULE: "module"
}
const { Search } = Input;


export const ModuleDetails = ({ setOrganizationName, organizationName }) => {
  const { orgid, id } = useParams();
  const [module, setModule] = useState([]);
  const [moduleName, setModuleName] = useState("...");
  const [loading, setLoading] = useState(false);

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

  useEffect(() => {
    setLoading(true);
    localStorage.setItem(ORGANIZATION_ARCHIVE, orgid);
    axiosInstance.get(`organization/${orgid}/module/${id}`)
      .then(response => {
        console.log(`organization/${orgid}/module/${id}`)
        console.log(response);
        setModule(response.data);
        setLoading(false);
        setModuleName(response.data.data.attributes.name);
      });

  }, [orgid, id]);

  const versions = (
    <Menu>
      <Menu.Item key="0">
        1.0.0
      </Menu.Item>
      <Menu.Item key="1">
        1.1.0
      </Menu.Item>
      <Menu.Item key="3">2.0.0</Menu.Item>
    </Menu>
  );

  return (
    <Content style={{ padding: '0 50px' }}>
      <Breadcrumb style={{ margin: '16px 0' }}>
        <Breadcrumb.Item>{organizationName}</Breadcrumb.Item>
        <Breadcrumb.Item><Link to={`/organizations/${orgid}/registry`}>Modules</Link></Breadcrumb.Item>
        <Breadcrumb.Item>{moduleName}</Breadcrumb.Item>
      </Breadcrumb>
      <div className="site-layout-content">
        {loading || !module.data ? (
          <p>Data loading...</p>
        ) : (
          <div>
            <Row>
              <Col span={17}>
                <Space direction="vertical" style={{ marginTop: "10px", width: "95%" }}>
                  <Tag color="blue"><span><MdBusiness /> Private</span></Tag>
                  <div>
                    <h2 className="moduleTitle">{module.data.attributes.name}</h2>
                    <span className="moduleDescription">{module.data.attributes.description}</span>
                  </div>
                  <Space className="moduleProvider" size="large" direction="horizontal">
                    <span>Published by {organizationName}</span>
                    <span>Provider {renderLogo(module.data.attributes.provider)} {module.data.attributes.provider}</span>
                  </Space>
                  <IconContext.Provider value={{ size: "1.3em" }}>
                    <table className="moduleDetails">
                      <tr>
                        <td><RiFolderHistoryLine /> Version</td>
                        <td><ClockCircleOutlined /> Published</td>
                        <td><DownloadOutlined /> Provisions</td>
                        <td><BiBookBookmark /> Source</td>
                      </tr>
                      <tr className="black">
                        <td>1.0.0 <Dropdown overlay={versions} trigger={['click']}>
                          <a className="ant-dropdown-link">
                            Change <DownOutlined />
                          </a>
                        </Dropdown>,</td>
                        <td>1 minute ago</td>
                        <td>&lt; 100</td>
                        <td><RiGithubFill /> <a href={module.data.attributes.source} target="_blank">{module.data.attributes.source.replace(".git", "").replace("https://github.com/", "")}</a></td>
                      </tr>
                    </table>
                  </IconContext.Provider>
                  <Tabs className="moduleTabs" defaultActiveKey="1" >
                    <TabPane tab="Readme" key="1">
                      Coming soon
                    </TabPane>
                    <TabPane tab="Inputs" key="2">
                      Coming soon
                    </TabPane>
                    <TabPane tab="Outputs" key="3">
                      Coming soon
                    </TabPane>
                    <TabPane tab="Dependencies" key="4">
                      Coming soon
                    </TabPane>
                    <TabPane tab="Resources" key="5">
                      Coming soon
                    </TabPane>
                  </Tabs>
                </Space>
              </Col>
              <Col  span={6}>
                <Card >
                  <Space style={{paddingRight:"10px"}} direction="vertical">
                    <p className="moduleSubtitles">Usage Instructions</p>
                    <p className="moduleInstructions">Copy and paste into your Terraform configuration and set values for the input variables.</p>
                    <div><Divider />
                    <p className="moduleSubtitles">Copy configuration details</p>
                    </div>
                    <pre className="moduleCode">
                      module "{module.data.attributes.name}" {"{"} <br />
                      &nbsp;&nbsp;source  = "registry.aks.vse.aespana.me/{module.data.attributes.registryPath}" <br />
                      &nbsp;&nbsp;version = "0.1.0" <br />
                      &nbsp;&nbsp;# insert required variables here <br />
                      {"}"}
                    </pre>
                    <Tag style={{ width: "290px", fontSize: "13px" }} color="blue">When running Terraform on the CLI, you must  <br />
                      configure credentials in .terraformrc or <br /> terraform.rc
                      to access this module: 
                      <pre className="moduleCredentials">
                        credentials "app.terraform.io" {"{"} <br />
                        &nbsp;&nbsp;# valid user API token:<br />
                        &nbsp;&nbsp;token = "xxxxxx.yyyyyy.zzzzzzzzzzzzz"<br />
                        {"}"}
                      </pre>

                    </Tag>
                    
                  </Space>
                </Card>
              </Col>
              <Col span={1}>

              </Col>
            </Row>


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