import { React, useEffect, useState } from "react";
import axiosInstance, { axiosClient } from "../../config/axiosConfig";
import { ORGANIZATION_ARCHIVE, WORKSPACE_ARCHIVE, ORGANIZATION_NAME } from '../../config/actionTypes';
import { Button, Layout, Breadcrumb, Tabs, List, Avatar, Row, Col, Tag, Form, Input, Select, Card, Space } from "antd";
import { compareVersions } from './Workspaces'
import { CreateJob } from '../Jobs/Create';
import { DetailsJob } from '../Jobs/Details';
import { Variables } from '../Workspaces/Variables';
import { States } from '../Workspaces/States';
import { Schedules } from "../Workspaces/Schedules";
import { useParams, Link } from "react-router-dom";
import {
  CheckCircleOutlined, ClockCircleOutlined, SyncOutlined
} from '@ant-design/icons';
import './Workspaces.css';
const { TabPane } = Tabs;
const { Option } = Select;
const include = {
  VARIABLE: 'variable',
  JOB: 'job',
  HISTORY: 'history',
  SCHEDULE: 'schedule'
}
const { DateTime } = require("luxon");


const { Content } = Layout;

export const WorkspaceDetails = (props) => {
  const { id } = useParams();
  const organizationId = localStorage.getItem(ORGANIZATION_ARCHIVE);
  localStorage.setItem(WORKSPACE_ARCHIVE, id);
  const [workspace, setWorkspace] = useState({});
  const [variables, setVariables] = useState([]);
  const [history, setHistory] = useState([]);
  const [schedule, setSchedule] = useState([]);
  const [envVariables, setEnvVariables] = useState([]);
  const [jobs, setJobs] = useState([]);
  const [stateDetailsVisible, setStateDetailsVisible] = useState(false);
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

  const handleStatesClick =(key) => {
    switchKey(key);
  };
  const callback = (key) => {
    switchKey(key);
  }

  const switchKey = (key) => {
    setActiveKey(key);
    if (key == "2") {
      setjobVisible(false);
    }
    if (key == "3") {
      setStateDetailsVisible(false);
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

    axiosInstance.get(`organization/${organizationId}/workspace/${id}?include=job,variable,history,schedule`)
      .then(response => {
        console.log(response);
        setWorkspace(response.data);
        if (response.data.included) {
          setupWorkspaceIncludes(response.data.included, setVariables, setJobs, setEnvVariables, setHistory,setSchedule);
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
              <Tabs activeKey={activeKey} defaultActiveKey="1" onTabClick={handleStatesClick} tabBarExtraContent={<CreateJob changeJob={changeJob} />} onChange={callback}>
                <TabPane tab="Runs" key="2">

                  {jobVisible ? (
                    <DetailsJob jobId={jobId} />
                  ) : (
                    <div>
                      <h3>Run List</h3>
                      <List 
                        itemLayout="horizontal"
                        dataSource={jobs.sort((a, b) => a.id.localeCompare(b.id)).reverse()}
                        renderItem={item => (
                          <List.Item  extra={
                            <div className="textLeft">
                              <Tag icon={item.status == "completed" ? <CheckCircleOutlined /> : (item.status == "running" ? <SyncOutlined spin /> : <ClockCircleOutlined />)} color={item.statusColor}>{item.status}</Tag> <br />
                              <span className="metadata">{item.latestChange}</span>
                            </div>
                          }>
                            <List.Item.Meta style={{margin:"0px", padding:"0px"}}
                              avatar={<Avatar shape="square" src="https://avatarfiles.alphacoders.com/128/thumb-128984.png" />}
                              title={<a onClick={() => handleClick(item.id)}>{item.title}</a>}
                              description={<span> #job-{item.id}  |  <b>jcanizalez</b> triggered via UI</span>}

                            />
                          </List.Item>
                        )} />
                    </div>
                  )}


                </TabPane>
                <TabPane  tab="States" key="3">
                  <States history={history} setStateDetailsVisible={setStateDetailsVisible} stateDetailsVisible={stateDetailsVisible} />
                </TabPane>
                <TabPane tab="Variables" key="4">
                  <Variables vars={variables} env={envVariables} />
                </TabPane>
                <TabPane tab="Schedules" key="5">
                  <Schedules schedules={schedule} />
                 </TabPane>
                <TabPane tab="Settings" key="6">
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
                          {terraformVersions.map(function (name, index) {
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

function setupWorkspaceIncludes(includes, setVariables, setJobs, setEnvVariables, setHistory,setSchedule) {
  let variables = [];
  let jobs = [];
  let envVariables = [];
  let history = [];
  let schedule = [];

  includes.forEach(element => {
    switch (element.type) {
      case include.JOB:
        jobs.push(
          {
            id: element.id,
            title: "Queue manually using Terraform",
            statusColor: element.attributes.status == "completed" ? "#2eb039" : (element.attributes.status == "running" ? "#108ee9" : ""),
            latestChange: DateTime.fromISO(element.attributes.createdDate).toRelative(),
            ...element.attributes
          }
        );
        break;
      case include.HISTORY:
        history.push(
          {
            id: element.id,
            title: "Queue manually using Terraform",
            relativeDate: DateTime.fromISO(element.attributes.createdDate).toRelative(),
            ...element.attributes,

          }
        );
        break;
        case include.SCHEDULE:
          schedule.push(
            {
              id: element.id,
              name: "Apply",
              ...element.attributes,
  
            }
          );
          break;
      case include.VARIABLE:
        if (element.attributes.category == "ENV") {
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
  setHistory(history);
  setSchedule(schedule);
}

