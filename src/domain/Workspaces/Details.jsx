import { React, useEffect, useState } from "react";
import axiosInstance, { axiosClient } from "../../config/axiosConfig";
import { ORGANIZATION_ARCHIVE, WORKSPACE_ARCHIVE ,ORGANIZATION_NAME} from '../../config/actionTypes';
import { Button, Layout, Breadcrumb, Table, Tabs, List, Avatar, Tag, Form, Input, Select } from "antd";
import { DeleteFilled } from '@ant-design/icons';
import { compareVersions } from './Workspaces'
import { CreateJob } from '../Jobs/Create';
import { DetailsJob } from '../Jobs/Details';
import {CreateVariable} from  '../Variables/Create';
import { useParams,Link } from "react-router-dom";
import {
  CheckCircleOutlined, ClockCircleOutlined,SyncOutlined
} from '@ant-design/icons';
import './Workspaces.css';
const { TabPane } = Tabs;
const { Option } = Select;
const include = {
  VARIABLE: 'variable',
  JOB: 'job'
}


const { Content } = Layout;

const VARIABLES_COLUMS = (organizationId, resourceId) => [
  {
    title: 'Key',
    dataIndex: 'key',
    key: 'key',
    render: (_, record) => {
      return  <div>{record.key} &nbsp;&nbsp;&nbsp;&nbsp; <Tag visible={record.hcl}>HCL</Tag> <Tag visible={record.sensitive}>Sensitive</Tag></div> ;
    }
  },
  {
    title: 'Value',
    dataIndex: 'value',
    key: 'value',
    render: (_, record) => {
      return record.sensitive ? <i>Sensitive - write only</i> : <div>{record.value}</div> ;
    }
  },
  {
    title: 'Actions',
    key: 'action',
    render: (_, record) => {
      return <Button icon={<DeleteFilled />} onClick={() => deleteVariable(record.id, record.type, organizationId, resourceId)}></Button>
    }
  }
]

export const WorkspaceDetails = (props) => {
  const { id } = useParams();
  const organizationId = localStorage.getItem(ORGANIZATION_ARCHIVE);
  localStorage.setItem(WORKSPACE_ARCHIVE, id);
  const [workspace, setWorkspace] = useState({});
  const [variables, setVariables] = useState([]);
  const [envVariables, setEnvVariables] = useState([]);
  const [jobs, setJobs] = useState([]);
  const [jobId, setJobId] = useState(0);
  const [loading, setLoading] = useState(false);
  const [jobVisible, setjobVisible] = useState(false);
  const [organizationName, setOrganizationName] = useState([]);
  const [workspaceName, setWorkspaceName] = useState("...");
  const [activeKey, setActiveKey] = useState("2");
  const [terraformVersions, setTerraformVersions] = useState([]);
  const terraformVersionsApi = "https://releases.hashicorp.com/terraform/index.json";
  const handleClick = id => {
    changeJob(id);
  };

  const callback = (key)  =>{
    setActiveKey(key);
    if (key =="2"){
      setjobVisible(false);
    }
  }
  useEffect(() => {
    setLoading(true);
    loadWorkspace();
    setLoading(false);
    axiosClient.get(terraformVersionsApi).then(
      resp => {
        console.log(resp);
        const tfVersions = [];
        for (const version in resp.data.versions) {
          if (!version.includes("-"))
              tfVersions.push(version)
        }
        setTerraformVersions(tfVersions.sort(compareVersions).reverse());
        console.log(tfVersions);
        
      }
    );
    const interval = setInterval(() => {
      loadWorkspace();
    }, 1000);
    return () => clearInterval(interval);
  }, [id]);
  
  const changeJob = id => {
    console.log(id);
    setJobId(id);
    setjobVisible(true);
    setActiveKey("2");
  }

  const loadWorkspace = () => {

    axiosInstance.get(`organization/${organizationId}/workspace/${id}?include=job,variable`)
      .then(response => {
        console.log(response);
        setWorkspace(response.data);
        if (response.data.included) {
          setupWorkspaceIncludes(response.data.included, setVariables, setJobs, setEnvVariables);
        }
        setOrganizationName(localStorage.getItem(ORGANIZATION_NAME));
        setWorkspaceName(response.data.data.attributes.name);
      });
  }

  return (
    <Content style={{ padding: '0 50px' }}>
      <Breadcrumb style={{ margin: '16px 0' }}>
        <Breadcrumb.Item>{organizationName}</Breadcrumb.Item>
        <Breadcrumb.Item><Link to={`/organizations/${organizationId}/workspaces`}>Workspaces</Link></Breadcrumb.Item>
        <Breadcrumb.Item>{workspaceName}</Breadcrumb.Item>
      </Breadcrumb>
      <div className="site-layout-content">
        <div className="workspaceDisplay">
          {loading || !workspace.data || !variables || !jobs ? (
            <p>Data loading...</p>
          ) : (
            <div className="orgWrapper">
             <div className='variableActions'> 
             <h2>{workspace.data.attributes.name}</h2>
             <table className="moduleDetails">
                      <tr>
                        <td>Resources</td>
                        <td>Terraform version</td>
                        <td>Updated</td>
                      </tr>
                      <tr className="black">
                        <td>0</td>
                        <td>{workspace.data.attributes.terraformVersion}</td>
                        <td>1 minute ago</td>
                      </tr>
                    </table>
             </div>
              <div className="App-text">
                No workspace description available. Add workspace description.
              </div>
              <Tabs activeKey={activeKey} defaultActiveKey="1" tabBarExtraContent={<CreateJob changeJob={changeJob}/>} onChange={callback}>
                <TabPane tab="Runs" key="2">
                  
                 {jobVisible ? (
                       <DetailsJob jobId={jobId}/>
                 ):(
                  <div>
                  <h3>Run List</h3>
                  <List
                    itemLayout="horizontal"
                    dataSource={jobs.sort((a, b) => a.id.localeCompare(b.id)).reverse()}
                    renderItem={item => (
                      <List.Item extra={
                        <div className="textLeft">
                          <Tag icon={item.status == "completed" ? <CheckCircleOutlined /> :(item.status == "running" ? <SyncOutlined spin />:  <ClockCircleOutlined />)} color={item.statusColor}>{item.status}</Tag> <br />
                        </div>
                      }>
                        <List.Item.Meta
                          avatar={<Avatar shape="square" src="https://avatarfiles.alphacoders.com/128/thumb-128984.png" />}
                          title={<a onClick={() => handleClick(item.id)}>{item.title}</a>}
                          description={<span>#job-{item.id}  |  <b>jcanizalez</b> triggered via UI</span>}

                        />
                      </List.Item>
                    )}/>
                  </div>
                 )}
                

                </TabPane>
                <TabPane tab="States" key="3">
                  Coming soon
                </TabPane>
                <TabPane tab="Variables" key="4">

                  <h1>Variables</h1>
                  <div className="App-text">
                    <p>These variables are used for all plans and applies in this workspace.Workspaces using Terraform 0.10.0 or later can also load default values from any *.auto.tfvars files in the configuration.</p>
                    <p>Sensitive variables are hidden from view in the UI and API, and can't be edited. (To change a sensitive variable, delete and replace it.) Sensitive variables can still appear in Terraform logs if your configuration is designed to output them.
                    </p> </div>
                  <h2>Terraform Variables</h2>
                  <div className="App-text">These Terraform variables are set using a terraform.tfvars file. To use interpolation or set a non-string value for a variable, click its HCL checkbox.</div>
                  <Table dataSource={variables} columns={VARIABLES_COLUMS(organizationId, id)} rowKey='key' />
                  <CreateVariable varType="TERRAFORM"/>
                  <div className="envVariables">
                    <h2>Environment Variables</h2>
                    <div className="App-text">These variables are set in Terraform's shell environment using export.</div>
                    <Table dataSource={envVariables} columns={VARIABLES_COLUMS(organizationId, id)} rowKey='key' />
                    <CreateVariable varType="ENV"/>
                  </div>
                </TabPane>
                <TabPane tab="Settings" key="5">
                  <div className="generalSettings">
                    <h1>General Settings</h1>
                    <Form layout="vertical" name="form-settings" >
                      <Form.Item name="id" label="ID" >
                        <div className="App-text">
                          {id}
                        </div>
                      </Form.Item>
                      <Form.Item name="name" label="Name" >
                        <Input defaultValue={workspace.data.attributes.name} />
                      </Form.Item>

                      <Form.Item name="description" label="Description">
                        <Input.TextArea placeholder="Workspace description" />
                      </Form.Item>
                      <Form.Item name="terraformVersion" label="Terraform Version">
                        <Select defaultValue={workspace.data.attributes.terraformVersion} style={{ width: 250 }}>
                        {terraformVersions.map(function(name, index){
                    return <Option key={name}>{name}</Option>;
                  })}
                        </Select>
                        <div className="App-text">
                        The version of Terraform to use for this workspace. Upon creating this workspace, the latest version was selected and will be used until it is changed manually. It will not upgrade automatically.
        </div>
                      </Form.Item>
                      <Form.Item>
                        <Button type="primary" htmlType="submit">
                          Save settings
                        </Button>
                      </Form.Item>
                    </Form>
                  </div>
                </TabPane>
              </Tabs>
            </div>
          )}
        </div>
      </div>
    </Content>
  )
}

function setupWorkspaceIncludes(includes, setVariables, setJobs, setEnvVariables) {
  let variables = [];
  let jobs = [];
  let envVariables = [];

  includes.forEach(element => {
    switch (element.type) {
      case include.JOB:
        jobs.push(
          {
            id: element.id,
            title: "Queue manually using Terraform",
            statusColor: element.attributes.status == "completed" ? "#2eb039" : (element.attributes.status == "running"?"#108ee9":""),
            latestChange: "1 minute ago",
            ...element.attributes
          }
        );
        break;
      case include.VARIABLE:
       if (element.attributes.category == "ENV"){
        envVariables.push(
          {
            id: element.id,
            type: element.type,
            ...element.attributes
          }
        );
       }
       else {
        variables.push(
          {
            id: element.id,
            type: element.type,
            ...element.attributes
          }
        );
       }
        break;
    }
  });

  setVariables(variables);
  setEnvVariables(envVariables);
  setJobs(jobs);
}

function deleteVariable(variableId, variableType, organizationId, resourceId) {
  console.log(variableId);

  axiosInstance.delete(`organization/${organizationId}/workspace/${resourceId}/${variableType}/${variableId}`, {
    headers: {
      'Content-Type': 'application/vnd.api+json'
    }
  })
    .then(response => {
      console.log(response);
    })
}