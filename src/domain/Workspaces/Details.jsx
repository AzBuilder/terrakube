import { React, useEffect, useState } from "react";
import axiosInstance from "../../config/axiosConfig";
import { ORGANIZATION_ARCHIVE, WORKSPACE_ARCHIVE } from '../../config/actionTypes';
import { Button, Layout, Breadcrumb, Table, Tabs, List, Avatar, Tag, Form, Input, Select } from "antd";
import { DeleteFilled } from '@ant-design/icons';
import {
  CheckCircleOutlined, ClockCircleOutlined
} from '@ant-design/icons';
import './Workspaces.css';
const { DateTime } = require("luxon");
const { TabPane } = Tabs;
const { Option } = Select;
const include = {
  ENVIRONMENT_VAR: 'environment',
  SECRET_VAR: 'secret',
  TERRAFORM_VAR: 'variable',
  JOB: 'job'
}


const { Content } = Layout;
function callback(key) {
  console.log(key);
}
const VARIABLES_COLUMS = (organizationId, resourceId) => [
  {
    title: 'Key',
    dataIndex: 'key',
    key: 'key'
  },
  {
    title: 'Value',
    dataIndex: 'value',
    key: 'value',
    render: (_, record) => {
      return record.type === 'secret' ? 'Hidden Value' : record.value;
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
  const resourceId = props.match.params.id;
  const organizationId = localStorage.getItem(ORGANIZATION_ARCHIVE);
  const [workspace, setWorkspace] = useState({});
  const [variables, setVariables] = useState([]);
  const [envVariables, setEnvVariables] = useState([]);
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    localStorage.setItem(WORKSPACE_ARCHIVE, resourceId);
    axiosInstance.get(`organization/${organizationId}/workspace/${resourceId}?include=environment,job,secret,variable`)
      .then(response => {
        console.log(response);
        setWorkspace(response.data);
        if (response.data.included) {
          setupWorkspaceIncludes(response.data.included, setVariables, setJobs, setEnvVariables);
        }
        setLoading(false);
      })
  }, [resourceId]);

  return (
    <Content style={{ padding: '0 50px' }}>
      <Breadcrumb style={{ margin: '16px 0' }}>
        <Breadcrumb.Item>organization-name</Breadcrumb.Item>
        <Breadcrumb.Item>Workspaces</Breadcrumb.Item>
        <Breadcrumb.Item>workpace_name</Breadcrumb.Item>
      </Breadcrumb>
      <div className="site-layout-content">
        <div className="workspaceDisplay">
          {loading || !workspace.data || !variables || !jobs ? (
            <p>Data loading...</p>
          ) : (
            <div className="orgWrapper">
              <h2>{workspace.data.attributes.name}</h2>
              <div className="App-text">
                No workspace description available. Add workspace description.
              </div>
              <Tabs defaultActiveKey="1" onChange={callback}>
                <TabPane tab="Runs" key="2">
                  <h3>Run List</h3>
                  <List
                    itemLayout="horizontal"
                    dataSource={jobs}
                    renderItem={item => (
                      <List.Item extra={
                        <div className="textLeft">
                          <Tag icon={item.status == "completed" ? <CheckCircleOutlined /> : <ClockCircleOutlined />} color={item.statusColor}>{item.status}</Tag> <br />
                        </div>
                      }>
                        <List.Item.Meta
                          avatar={<Avatar shape="square" src="https://avatarfiles.alphacoders.com/128/thumb-128984.png" />}
                          title={item.title}
                          description={<span>#job-{item.id}  |  <b>Rick</b> triggered via CLI</span>}

                        />
                      </List.Item>
                    )}
                  />

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
                  <Table dataSource={variables} columns={VARIABLES_COLUMS(organizationId, resourceId)} rowKey='key' />
                  <Button type="primary" htmlType="button" href={`/workspaces/${workspace.data.attributes.name}/variable/create`}>Add Variable</Button>
                  <div className="envVariables">
                    <h2>Environment Variables</h2>
                    <div className="App-text">These variables are set in Terraform's shell environment using export.</div>
                    <Table dataSource={envVariables} columns={VARIABLES_COLUMS(organizationId, resourceId)} rowKey='key' />
                    <Button type="primary" htmlType="button" href={`/workspaces/${workspace.data.attributes.name}/variable/create`}>Add Variable</Button>
                  </div>
                </TabPane>
                <TabPane tab="Settings" key="5">
                  <div className="generalSettings">
                    <h1>General Settings</h1>
                    <Form layout="vertical" name="form-settings" >
                      <Form.Item name="id" label="ID" >
                        <div className="App-text">
                          {resourceId}
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
                          <Option value="0.13.0">0.13.0</Option>
                          <Option value="0.14.0">0.14.0</Option>
                          <Option value="0.14.1">0.14.1</Option>
                          <Option value="0.14.2">0.14.2</Option>
                          <Option value="0.14.3">0.14.3</Option>
                          <Option value="0.15.0">0.15.0</Option>
                          <Option value="0.15.1">0.15.1</Option>
                          <Option value="0.15.2">0.15.2</Option>
                          <Option value="0.15.3">0.15.3</Option>
                          <Option value="1.0.0">1.0.0</Option>
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
            statusColor: element.attributes.status == "completed" ? "#2eb039" : "",
            latestChange: DateTime.local().minus({ minutes: Math.floor(Math.random() * 5) }).toRelative(),
            ...element.attributes
          }
        );
        break;
      case include.ENVIRONMENT_VAR:
        envVariables.push(
          {
            id: element.id,
            type: element.type,
            ...element.attributes
          }
        );
        break;
      default:
        variables.push(
          {
            id: element.id,
            type: element.type,
            ...element.attributes
          }
        );
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