import { React, useState, useEffect } from 'react';
import { Menu, Layout, Breadcrumb, Dropdown, Tabs, Space, Tag, Row, Col, Card, Divider } from "antd";
import { useParams, Link } from "react-router-dom";
import axiosInstance from "../../config/axiosConfig";
import { DownOutlined, CloudOutlined, ClockCircleOutlined, DownloadOutlined } from '@ant-design/icons';
import { GitlabOutlined,GithubOutlined } from '@ant-design/icons';
import { SiBitbucket, SiAzuredevops,SiMicrosoftazure, SiAmazonaws } from "react-icons/si";
import { BiBookBookmark } from "react-icons/bi";
import { RiFolderHistoryLine } from "react-icons/ri";
import { IconContext } from "react-icons";
import { MdBusiness } from 'react-icons/md';
import ReactMarkdown from 'react-markdown'
import { compareVersions } from '../Workspaces/Workspaces'
import {unzip} from 'unzipit';
import './Module.css';
import { ORGANIZATION_ARCHIVE } from '../../config/actionTypes';
import {Buffer} from 'buffer';

const { TabPane } = Tabs;
const { Content } = Layout;

export const ModuleDetails = ({ setOrganizationName, organizationName }) => {
  const { orgid, id } = useParams();
  const [module, setModule] = useState([]);
  const [moduleName, setModuleName] = useState("...");
  const [version, setVersion] = useState("...");
  const [vcsProvider, setVCSProvider] = useState("");
  const [loading, setLoading] = useState(false);
  const [markdown, setMarkdown] = useState("loading...");
  const renderLogo = (provider) => {
    switch (provider) {
      case 'azurerm':
        return <IconContext.Provider value={{ color: "#008AD7", size: "1.5em" }}><SiMicrosoftazure /></IconContext.Provider>;
      case 'aws':
        return <IconContext.Provider value={{ color: "#232F3E", size: "1.5em" }}><SiAmazonaws /></IconContext.Provider>;
      default:
        return <CloudOutlined />;
    }
  }
  const handleClick = e => {
    setMarkdown("loading...");
    setVersion(e.key);
    loadReadme(module.data.attributes.registryPath,e.key)
  };

  async function readFiles(url) {
    const {entries} = await unzip(url);

    if(entries['README.md'] != null){
    const readmeFile = await entries['README.md'].blob();

    if(readmeFile != null)
    {
       const text = await readmeFile.text();
       setMarkdown(text);
      
    }
  }
  else
  {
    setMarkdown("");
  }
}

async function loadReadmeFile(text) {

  if(text != null)
  {
     const textReadme = Buffer.from(text, "base64").toString();
     setMarkdown(textReadme);
    
  }
  else
  {
    setMarkdown("");
  }
}

  useEffect(() => {
    setLoading(true);
    localStorage.setItem(ORGANIZATION_ARCHIVE, orgid);
    axiosInstance.get(`organization/${orgid}/module/${id}?include=vcs`)
      .then(response => {
        console.log(`organization/${orgid}/module/${id}`)
        console.log(response);
        setModule(response.data);
        setLoading(false);
        setModuleName(response.data.data.attributes.name);
        if(response.data.included != null && response.data.included[0] != null)
        {
          setVCSProvider(response.data.included[0].attributes.vcsType);
        }

        
        setVersion(response.data.data.attributes.versions.sort(compareVersions).reverse()[0]); // latest version
        loadReadme(response.data.data.attributes.registryPath,response.data.data.attributes.versions[0]);
      });
      

  }, [orgid, id]);

 
  const loadReadme = (path,version) => {
    axiosInstance.get(`${window._env_.REACT_APP_REGISTRY_URI}/terraform/readme/v1/${path}/${version}/download`).then(
      resp => {
        console.log(resp);
        console.log('Headers')
        console.log(resp.headers);
        loadReadmeFile(resp.data.content);
      }
    );
    
  }

  const renderVCSLogo = (vcs) => {
    switch (vcs) {
      case 'GITLAB':
        return <GitlabOutlined style={{ fontSize: '18px' }} />;
      case 'BITBUCKET':
        return <IconContext.Provider value={{ size: "18px" }}><SiBitbucket />&nbsp;</IconContext.Provider>;
      case 'AZURE_DEVOPS':
        return <IconContext.Provider value={{ size: "18px" }}><SiAzuredevops />&nbsp;</IconContext.Provider>;
      default:
        return <GithubOutlined style={{ fontSize: '18px' }} />;
    }
  }

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
                        <td>{version} <Dropdown overlay={<Menu onClick={handleClick}> {module.data.attributes.versions.sort(compareVersions).reverse().map(function(name, index){
                    return <Menu.Item key={name}>{name}</Menu.Item>;
                  })
                          
                }</Menu>} trigger={['click']}>
                          <a className="ant-dropdown-link">
                            Change <DownOutlined />
                          </a>
                        </Dropdown>,</td>
                        <td>1 minute ago</td>
                        <td>&nbsp; {module.data.attributes.downloadQuantity}</td>
                        <td>{renderVCSLogo(vcsProvider)} <a href={module.data.attributes.source} target="_blank">{module.data.attributes.source.replace(".git", "").replace("https://github.com/", "")}</a></td>
                      </tr>
                    </table>
                  </IconContext.Provider>
                  <Tabs className="moduleTabs" defaultActiveKey="1" >
                    <TabPane className="markdown-body" tab="Readme" key="1">
                      <ReactMarkdown>{markdown}</ReactMarkdown>
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
              <Col span={7}>
                <Card >
                  <Space style={{ paddingRight: "10px" }} direction="vertical">
                    <p className="moduleSubtitles">Usage Instructions</p>
                    <p className="moduleInstructions">Copy and paste into your Terraform configuration and set values for the input variables.</p>
                    <div style={{ width: "65%" }}><Divider />
                      <p className="moduleSubtitles">Copy configuration details</p>
                    </div>
                    <pre className="moduleCode">
                      module "{module.data.attributes.name}" {"{"} <br />
                      &nbsp;&nbsp;source  = "{new URL(window._env_.REACT_APP_REGISTRY_URI).hostname}/{module.data.attributes.registryPath}" <br />
                      &nbsp;&nbsp;version = "{version}" <br />
                      &nbsp;&nbsp;# insert required variables here <br />
                      {"}"}
                    </pre>
                    <Tag style={{ width: "65%", fontSize: "13px" }} color="blue">When running Terraform on the CLI, you must  <br />
                      configure credentials in .terraformrc or <br /> terraform.rc
                      to access this module:
                      <pre className="moduleCredentials">
                        credentials "app.terrakube.io" {"{"} <br />
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