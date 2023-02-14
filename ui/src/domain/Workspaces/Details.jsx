import { React, useEffect, useState } from "react";
import axiosInstance, { axiosClient } from "../../config/axiosConfig";
import { HiOutlineExternalLink } from "react-icons/hi";
import {
  ORGANIZATION_ARCHIVE,
  WORKSPACE_ARCHIVE,
  ORGANIZATION_NAME,
} from "../../config/actionTypes";
import {
  Button,
  Layout,
  Breadcrumb,
  Tabs,
  List,
  Space,
  Avatar,
  Tag,
  Form,
  Input,
  Select,
  message,
  Spin,
  Switch,
  Typography,
  Popconfirm,
} from "antd";
import { compareVersions } from "./Workspaces";
import { CreateJob } from "../Jobs/Create";
import { DetailsJob } from "../Jobs/Details";
import { Variables } from "../Workspaces/Variables";
import { States } from "../Workspaces/States";
import { Schedules } from "../Workspaces/Schedules";
import { useParams, Link } from "react-router-dom";
import {
  CheckCircleOutlined,
  ClockCircleOutlined,
  SyncOutlined,
  ExclamationCircleOutlined,
  StopOutlined,
  InfoCircleOutlined,
  DeleteOutlined,
  UserOutlined,
} from "@ant-design/icons";
import "./Workspaces.css";
const { TabPane } = Tabs;
const { Option } = Select;
const { Paragraph } = Typography;
const include = {
  VARIABLE: "variable",
  JOB: "job",
  HISTORY: "history",
  SCHEDULE: "schedule",
};
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
  const [waiting, setWaiting] = useState(false);
  const [templates, setTemplates] = useState([]);
  const [lastRun, setLastRun] = useState("");

  const terraformVersionsApi = `${
    new URL(window._env_.REACT_APP_TERRAKUBE_API_URL).origin
  }/terraform/index.json`;
  const handleClick = (id) => {
    changeJob(id);
  };

  const handleStatesClick = (key) => {
    switchKey(key);
  };
  const callback = (key) => {
    switchKey(key);
  };

  const switchKey = (key) => {
    setActiveKey(key);
    if (key == "2") {
      setjobVisible(false);
    }
    if (key == "3") {
      setStateDetailsVisible(false);
    }
  };
  useEffect(() => {
    setLoading(true);
    loadWorkspace();
    axiosClient.get(terraformVersionsApi).then((resp) => {
      const tfVersions = [];
      for (const version in resp.data.versions) {
        if (!version.includes("-")) tfVersions.push(version);
      }
      setTerraformVersions(tfVersions.sort(compareVersions).reverse());
    });
    setLoading(false);

    const interval = setInterval(() => {
      loadWorkspace();
    }, 15000);
    return () => clearInterval(interval);
  }, [id]);

  const changeJob = (id) => {
    console.log(id);
    setJobId(id);
    setjobVisible(true);
    setActiveKey("2");
  };

  const loadWorkspace = () => {
    axiosInstance
      .get(`organization/${organizationId}/template`)
      .then((template) => {
        setTemplates(template.data.data);
        axiosInstance
          .get(
            `organization/${organizationId}/workspace/${id}?include=job,variable,history,schedule`
          )
          .then((response) => {
            setWorkspace(response.data);
            if (
              response.data.data.attributes.source === "empty" &&
              response.data.data.attributes.branch === "remote-content"
            ) {
              switchKey("1");
            }

            if (response.data.included) {
              setupWorkspaceIncludes(
                response.data.included,
                setVariables,
                setJobs,
                setEnvVariables,
                setHistory,
                setSchedule,
                template.data.data,
                setLastRun
              );
            }
            setOrganizationName(localStorage.getItem(ORGANIZATION_NAME));
            setWorkspaceName(response.data.data.attributes.name);
          });
      });
  };

  const onFinish = (values) => {
    setWaiting(true);
    const body = {
      data: {
        type: "workspace",
        id: id,
        attributes: {
          name: values.name,
          description: values.description,
          folder: values.folder,
          locked: values.locked,
          terraformVersion: values.terraformVersion,
        },
      },
    };
    console.log(body);

    axiosInstance
      .patch(`organization/${organizationId}/workspace/${id}`, body, {
        headers: {
          "Content-Type": "application/vnd.api+json",
        },
      })
      .then((response) => {
        console.log(response);
        if (response.status == "204") {
          message.success("Workspace updated successfully");
        } else {
          message.error("Workspace update failed");
        }
        setWaiting(false);
      });
  };

  const onDelete = (values) => {
    const body = {
      data: {
        type: "workspace",
        id: id,
        attributes: {
          deleted: "true",
        },
      },
    };

    axiosInstance
      .patch(`organization/${organizationId}/workspace/${id}`, body, {
        headers: {
          "Content-Type": "application/vnd.api+json",
        },
      })
      .then((response) => {
        console.log(response);
        if (response.status == "204") {
          console.log(response);
          message.success("Workspace deleted successfully");
          history.push(`/organizations/${organizationId}/workspaces`);
        } else {
          message.error("Workspace deletion failed");
        }
      });
  };

  return (
    <Content style={{ padding: "0 50px" }}>
      <Breadcrumb style={{ margin: "16px 0" }}>
        <Breadcrumb.Item>{organizationName}</Breadcrumb.Item>
        <Breadcrumb.Item>
          <Link to={`/organizations/${organizationId}/workspaces`}>
            Workspaces
          </Link>
        </Breadcrumb.Item>
        <Breadcrumb.Item>{workspaceName}</Breadcrumb.Item>
      </Breadcrumb>
      <div className="site-layout-content">
        <div className="workspaceDisplay">
          {loading || !workspace.data || !variables || !jobs ? (
            <p>Data loading...</p>
          ) : (
            <div className="orgWrapper">
              <div className="variableActions">
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
                    <td>
                      {DateTime.fromISO(lastRun).toRelative() ??
                        "never executed"}
                    </td>
                  </tr>
                </table>
              </div>
              <div className="App-text">
                {workspace.data.attributes.description}
              </div>
              <Tabs
                activeKey={activeKey}
                onTabClick={handleStatesClick}
                tabBarExtraContent={<CreateJob changeJob={changeJob} />}
                onChange={callback}
              >
                {workspace.data.attributes.source === "empty" &&
                  workspace.data.attributes.branch === "remote-content" && (
                    <TabPane tab="Overview" key="1">
                      <div>
                        <h1>Waiting for configuration</h1>
                        <div className="App-text">
                          This workspace currently has no Terraform
                          configuration files associated with it. Terrakube is
                          waiting for the configuration to be uploaded.
                        </div>
                        <h3>CLI-driven workflow</h3>
                        <div className="App-text">
                          <ol>
                            <li>
                              Ensure you are properly authenticated into
                              Terrakube by running{" "}
                              <span className="code">terraform login</span> on
                              the command line or by using a credentials block.
                            </li>{" "}
                            <br />
                            <li>
                              Add a code block to your Terraform configuration
                              files to set up the remote backend . You can add
                              this configuration block to any .tf file in the
                              directory where you run Terraform. <br />
                              <br />
                              <b>Example Code</b>
                              <pre className="moduleCode">
                                terraform {"{"} <br />
                                &nbsp;&nbsp;backend "remote" {"{"} <br />
                                &nbsp;&nbsp;&nbsp;&nbsp;organization = "
                                {organizationName}" <br />
                                <br />
                                &nbsp;&nbsp;&nbsp;&nbsp;workspaces {"{"} <br />
                                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;name = "
                                {workspaceName}" <br />
                                &nbsp;&nbsp;&nbsp;&nbsp;{"}"} <br />
                                &nbsp;&nbsp;{"}"} <br />
                                {"}"} <br />
                              </pre>
                            </li>
                            <br />
                            <li>
                              Run <span className="code">terraform init</span>{" "}
                              to initialize the workspace.
                            </li>
                            <br />
                            <li>
                              Run <span className="code">terraform apply</span>{" "}
                              to start the first run for this workspace.
                            </li>
                          </ol>
                          For more details, see the{" "}
                          <Button
                            className="link"
                            target="_blank"
                            href="https://docs.terrakube.org/user-guide/workspaces/cli-driven-workflow"
                            type="link"
                          >
                            CLI workflow guide.&nbsp; <HiOutlineExternalLink />.
                          </Button>
                          <br /> <br />
                          <h3>API-driven workflow</h3>
                          Advanced users can follow{" "}
                          <Button
                            className="link"
                            target="_blank"
                            href="https://docs.terrakube.org/user-guide/workspaces/api-driven-workflow"
                            type="link"
                          >
                            this guide.&nbsp; <HiOutlineExternalLink />.
                          </Button>{" "}
                          to set up their workspace.
                        </div>
                      </div>
                    </TabPane>
                  )}

                <TabPane tab="Runs" key="2">
                  {jobVisible ? (
                    <DetailsJob jobId={jobId} />
                  ) : (
                    <div>
                      <h3>Run List</h3>
                      <List
                        itemLayout="horizontal"
                        dataSource={jobs.sort((a, b) => a.id - b.id).reverse()}
                        renderItem={(item) => (
                          <List.Item
                            extra={
                              <div className="textLeft">
                                <Tag
                                  icon={
                                    item.status == "completed" ? (
                                      <CheckCircleOutlined />
                                    ) : item.status == "running" ? (
                                      <SyncOutlined spin />
                                    ) : item.status === "waitingApproval" ? (
                                      <ExclamationCircleOutlined />
                                    ) : item.status === "cancelled" ? (
                                      <StopOutlined />
                                    ) : item.status === "failed" ? (
                                      <StopOutlined />
                                    ) : (
                                      <ClockCircleOutlined />
                                    )
                                  }
                                  color={item.statusColor}
                                >
                                  {item.status}
                                </Tag>{" "}
                                <br />
                                <span className="metadata">
                                  {item.latestChange}
                                </span>
                              </div>
                            }
                          >
                            <List.Item.Meta
                              style={{ margin: "0px", padding: "0px" }}
                              avatar={
                                <Avatar
                                  shape="square"
                                  icon={<UserOutlined />}
                                />
                              }
                              title={
                                <a onClick={() => handleClick(item.id)}>
                                  {item.title}
                                </a>
                              }
                              description={
                                <span>
                                  {" "}
                                  #job-{item.id} | commitId{" "}
                                  {item.commitId?.substring(0, 6)} |{" "}
                                  <b>{item.createdBy}</b> triggered via UI
                                </span>
                              }
                            />
                          </List.Item>
                        )}
                      />
                    </div>
                  )}
                </TabPane>
                <TabPane tab="States" key="3">
                  <States
                    history={history}
                    setStateDetailsVisible={setStateDetailsVisible}
                    stateDetailsVisible={stateDetailsVisible}
                  />
                </TabPane>
                <TabPane tab="Variables" key="4">
                  <Variables vars={variables} env={envVariables} />
                </TabPane>
                <TabPane tab="Schedules" key="5">
                  {templates ? (
                    <Schedules schedules={schedule} />
                  ) : (
                    <p>Loading...</p>
                  )}
                </TabPane>
                <TabPane tab="Settings" key="6">
                  <div className="generalSettings">
                    <h1>General Settings</h1>
                    <Spin spinning={waiting}>
                      <Form
                        onFinish={onFinish}
                        initialValues={{
                          name: workspace.data.attributes.name,
                          description: workspace.data.attributes.description,
                          folder: workspace.data.attributes.folder,
                          locked: workspace.data.attributes.locked,
                        }}
                        layout="vertical"
                        name="form-settings"
                      >
                        <Form.Item name="id" label="ID">
                          <Paragraph copyable={{ tooltips: false }}>
                            <span className="App-text"> {id}</span>
                          </Paragraph>
                        </Form.Item>
                        <Form.Item name="name" label="Name">
                          <Input />
                        </Form.Item>

                        <Form.Item
                          valuePropName="value"
                          name="description"
                          label="Description"
                        >
                          <Input.TextArea placeholder="Workspace description" />
                        </Form.Item>
                        <Form.Item
                          name="terraformVersion"
                          label="Terraform Version"
                          extra="The version of Terraform to use for this workspace.
                          Upon creating this workspace, the latest version was
                          selected and will be used until it is changed
                          manually. It will not upgrade automatically."
                        >
                          <Select
                            defaultValue={
                              workspace.data.attributes.terraformVersion
                            }
                            style={{ width: 250 }}
                          >
                            {terraformVersions.map(function (name, index) {
                              return <Option key={name}>{name}</Option>;
                            })}
                          </Select>
                        </Form.Item>
                        <Form.Item
                          name="folder"
                          label="Terraform Working Directory"
                          extra="The directory that Terraform will execute within. This defaults to the root of your repository and is typically set to a subdirectory matching the environment when multiple environments exist within the same repository."
                        >
                          <Input />
                        </Form.Item>
                        <Form.Item
                          name="locked"
                          valuePropName="checked"
                          label="Lock Workspace"
                          tooltip={{
                            title: "Lock Workspace",
                            icon: <InfoCircleOutlined />,
                          }}
                        >
                          <Switch />
                        </Form.Item>

                        <Form.Item>
                          <Button type="primary" htmlType="submit">
                            Save settings
                          </Button>
                        </Form.Item>
                      </Form>
                    </Spin>
                    <h1>Delete Workspace</h1>
                    <div className="App-Text">
                      Deleting the workspace will permanently delete the
                      information. Please be certain that you understand this.
                      This action cannot be undone.
                    </div>
                    <Popconfirm
                      onConfirm={() => {
                        onDelete(id);
                      }}
                      style={{ width: "100%" }}
                      title={
                        <p>
                          Workspace will be permanently deleted <br /> from this
                          organization.
                          <br />
                          Are you sure?
                        </p>
                      }
                      okText="Yes"
                      cancelText="No"
                      placement="bottom"
                    >
                      <Button type="default" danger style={{ width: "100%" }}>
                        <Space>
                          <DeleteOutlined />
                          Delete from Terrakube
                        </Space>
                      </Button>
                    </Popconfirm>
                  </div>
                </TabPane>
              </Tabs>
            </div>
          )}
        </div>
      </div>
    </Content>
  );
};

function setupWorkspaceIncludes(
  includes,
  setVariables,
  setJobs,
  setEnvVariables,
  setHistory,
  setSchedule,
  templates,
  setLastRun
) {
  let variables = [];
  let jobs = [];
  let envVariables = [];
  let history = [];
  let schedule = [];

  includes.forEach((element) => {
    switch (element.type) {
      case include.JOB:
        let finalColor = "";
        switch (element.attributes.status) {
          case "completed":
            finalColor = "#2eb039";
            break;
          case "rejected":
            finalColor = "#FB0136";
            break;
          case "failed":
            finalColor = "#FB0136";
            break;
          case "running":
            finalColor = "#108ee9";
            break;
          case "waitingApproval":
            finalColor = "#fa8f37";
            break;
          default:
            finalColor = "";
            break;
        }
        jobs.push({
          id: element.id,
          title: "Queue manually using Terraform",
          statusColor: finalColor,
          commitId: element.attributes.commitId,
          stepNumber: element.attributes.stepNumber,
          latestChange: DateTime.fromISO(
            element.attributes.createdDate
          ).toRelative(),
          ...element.attributes,
        });
        setLastRun(element.attributes.updatedDate);
        break;
      case include.HISTORY:
        history.push({
          id: element.id,
          title: "Queue manually using Terraform",
          relativeDate: DateTime.fromISO(
            element.attributes.createdDate
          ).toRelative(),
          ...element.attributes,
        });
        break;

      case include.SCHEDULE:
        schedule.push({
          id: element.id,
          name: templates?.find(
            (template) => template.id === element.attributes.templateReference
          )?.attributes?.name,
          ...element.attributes,
        });
        break;
      case include.VARIABLE:
        if (element.attributes.category == "ENV") {
          envVariables.push({
            id: element.id,
            type: element.type,
            ...element.attributes,
          });
        } else {
          variables.push({
            id: element.id,
            type: element.type,
            ...element.attributes,
          });
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
