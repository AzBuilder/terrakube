import { React, useEffect, useState } from "react";
import axiosInstance, { axiosClient } from "../../config/axiosConfig";
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
  Row,
  Col,
  Divider,
  Table,
} from "antd";
import { compareVersions } from "./Workspaces";
import { CreateJob } from "../Jobs/Create";
import { DetailsJob } from "../Jobs/Details";
import { Variables } from "../Workspaces/Variables";
import { States } from "../Workspaces/States";
import { Schedules } from "../Workspaces/Schedules";
import { CLIDriven } from "../Workspaces/CLIDriven";
import { Tags } from "../Workspaces/Tags";
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
  ThunderboltOutlined,
  PlayCircleOutlined,
  LockOutlined,
  ProfileOutlined,
  UnlockOutlined,
  GitlabOutlined,
  GithubOutlined,
} from "@ant-design/icons";
import { SiTerraform, SiBitbucket, SiAzuredevops } from "react-icons/si";
import { IconContext } from "react-icons";
import { FiGitCommit } from "react-icons/fi";
import { BiTerminal } from "react-icons/bi";
import "./Workspaces.css";
const { TabPane } = Tabs;
const { Option } = Select;
const { Paragraph } = Typography;
const include = {
  VARIABLE: "variable",
  JOB: "job",
  HISTORY: "history",
  SCHEDULE: "schedule",
  VCS: "vcs",
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
  const [activeKey, setActiveKey] = useState("1");
  const [terraformVersions, setTerraformVersions] = useState([]);
  const [waiting, setWaiting] = useState(false);
  const [templates, setTemplates] = useState([]);
  const [lastRun, setLastRun] = useState("");
  const [executionMode, setExecutionMode] = useState("...");
  const [vcsProvider, setVCSProvider] = useState("");
  const [resources, setResources] = useState([]);
  const [outputs, setOutputs] = useState([]);
  const [currentStateId, setCurrentStateId] = useState(0);
  const terraformVersionsApi = `${
    new URL(window._env_.REACT_APP_TERRAKUBE_API_URL).origin
  }/terraform/index.json`;
  const handleClick = (id) => {
    changeJob(id);
  };
  const outputColumns = [
    {
      title: "Name",
      dataIndex: "name",
      key: "name",
      sorter: (a, b) => a.name.localeCompare(b.name),
    },
    {
      title: "Type",
      dataIndex: "type",
      key: "type",
    },
    {
      title: "Value",
      dataIndex: "value",
      key: "value",
      render: (text) => (
        <Paragraph style={{ margin: "0px" }} copyable={{ tooltips: false }}>
          {text}
        </Paragraph>
      ),
    },
  ];

  const resourceColumns = [
    {
      title: "Name",
      dataIndex: "name",
      key: "name",
      sorter: (a, b) => a.name.localeCompare(b.name),
    },
    {
      title: "Provider",
      dataIndex: "provider",
      key: "provider",
    },
    {
      title: "Type",
      dataIndex: "type",
      key: "type",
    },
    {
      title: "Module",
      dataIndex: "module",
      key: "module",
    },
  ];
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
    axiosInstance.get(terraformVersionsApi).then((resp) => {
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
            `organization/${organizationId}/workspace/${id}?include=job,variable,history,schedule,vcs`
          )
          .then((response) => {
            setWorkspace(response.data);
            console.log(response.data);
            if (response.data.included) {
              setupWorkspaceIncludes(
                response.data.included,
                setVariables,
                setJobs,
                setEnvVariables,
                setHistory,
                setSchedule,
                template.data.data,
                setLastRun,
                setVCSProvider,
                setCurrentStateId,
                currentStateId,
                axiosInstance,
                setResources,
                setOutputs
              );
            }
            setOrganizationName(localStorage.getItem(ORGANIZATION_NAME));
            setWorkspaceName(response.data.data.attributes.name);
            setExecutionMode(response.data.data.attributes.executionMode);
          });
      });
  };

  const handleClickSettings = () => {
    setActiveKey("6");
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
          executionMode: values.executionMode,
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

  const renderVCSLogo = (vcs) => {
    switch (vcs) {
      case "GITLAB":
        return <GitlabOutlined style={{ fontSize: "18px" }} />;
      case "BITBUCKET":
        return (
          <IconContext.Provider value={{ size: "18px" }}>
            <SiBitbucket />
            &nbsp;
          </IconContext.Provider>
        );
      case "AZURE_DEVOPS":
        return (
          <IconContext.Provider value={{ size: "18px" }}>
            <SiAzuredevops />
            &nbsp;
          </IconContext.Provider>
        );
      default:
        return <GithubOutlined style={{ fontSize: "18px" }} />;
    }
  };

  const onDelete = (workspace) => {
    let randomLetters = generateRandomString(4);
    let deletedName = `${workspace.data.attributes.name.substring(
      0,
      21
    )}_DEL_${randomLetters}`;
    console.log(`New deleted name; ${deletedName}`);
    const body = {
      data: {
        type: "workspace",
        id: id,
        attributes: {
          name: deletedName,
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
              </div>
              <Space className="workspace-details" direction="vertical">
                <Paragraph
                  style={{ margin: "0px" }}
                  copyable={{ text: id, tooltips: false }}
                >
                  <span className="workspace-details"> ID: {id} </span>
                </Paragraph>
                {workspace.data.attributes.description === "" ? (
                  workspace.data.attributes.description
                ) : (
                  <a
                    className="workspace-button"
                    onClick={handleClickSettings}
                    style={{ color: "#3b3d45" }}
                  >
                    Add workspace description
                  </a>
                )}
                <Space
                  size={40}
                  style={{ marginBottom: "40px" }}
                  direction="horizontal"
                >
                  <span>
                    {workspace.data.attributes.locked ? (
                      <>
                        <LockOutlined /> Locked
                      </>
                    ) : (
                      <>
                        <UnlockOutlined /> Unlocked
                      </>
                    )}
                  </span>
                  <span>
                    <ProfileOutlined /> Resources{" "}
                    <span style={{ fontWeight: "500" }}>
                      {resources.length}
                    </span>
                  </span>
                  <IconContext.Provider value={{ size: "1.0em" }}>
                    <SiTerraform /> Terraform{" "}
                    <a
                      onClick={handleClickSettings}
                      className="workspace-button"
                      style={{ color: "#3b3d45" }}
                    >
                      v{workspace.data.attributes.terraformVersion}
                    </a>
                  </IconContext.Provider>
                  <span>
                    <ClockCircleOutlined /> Updated{" "}
                    <span style={{ fontWeight: "500" }}>
                      {DateTime.fromISO(lastRun).toRelative() ??
                        "never executed"}
                    </span>
                  </span>
                </Space>
              </Space>
              <Tabs
                activeKey={activeKey}
                onTabClick={handleStatesClick}
                tabBarExtraContent={<CreateJob changeJob={changeJob} />}
                onChange={callback}
              >
                <TabPane tab="Overview" key="1">
                  <Row>
                    <Col span={19} style={{paddingRight:"20px"}}>
                      {workspace.data.attributes.source === "empty" &&
                      workspace.data.attributes.branch === "remote-content" ? (
                        <CLIDriven
                          organizationName={organizationName}
                          workspaceName={workspaceName}
                        />
                      ) : (
                        <div>
                          <h3>Latest Run</h3>
                          <div
                            style={{ marginRight: "150px", borderWidth: "1px" }}
                          >
                            <List
                              itemLayout="horizontal"
                              style={{
                                border: "1px solid #c2c5cb",
                                padding: "24px",
                              }}
                              dataSource={
                                jobs.length > 0
                                  ? jobs
                                      .sort((a, b) => a.id - b.id)
                                      .reverse()
                                      .slice(0, 1)
                                  : []
                              }
                              renderItem={(item) => (
                                <List.Item>
                                  <List.Item.Meta
                                    style={{ margin: "0px", padding: "0px" }}
                                    avatar={
                                      <Avatar
                                        shape="square"
                                        icon={<UserOutlined />}
                                      />
                                    }
                                    description={
                                      <div>
                                        <Row>
                                          <Col span={20}>
                                            <h4 className="ant-list-item-meta-title">
                                              <a
                                                onClick={() =>
                                                  handleClick(item.id)
                                                }
                                              >
                                                {item.title}
                                              </a>{" "}
                                            </h4>
                                            <b>{item.createdBy}</b> triggered a
                                            run {item.latestChange} via{" "}
                                            <b>UI</b>{" "}
                                            {item.commitId !== "000000000" ? (
                                              <>
                                                <FiGitCommit />{" "}
                                                {item.commitId?.substring(0, 6)}{" "}
                                              </>
                                            ) : (
                                              ""
                                            )}
                                          </Col>
                                          <Col>
                                            {
                                              <div className="textLeft">
                                                <Tag
                                                  icon={
                                                    item.status ==
                                                    "completed" ? (
                                                      <CheckCircleOutlined />
                                                    ) : item.status ==
                                                      "running" ? (
                                                      <SyncOutlined spin />
                                                    ) : item.status ===
                                                      "waitingApproval" ? (
                                                      <ExclamationCircleOutlined />
                                                    ) : item.status ===
                                                      "cancelled" ? (
                                                      <StopOutlined />
                                                    ) : item.status ===
                                                      "failed" ? (
                                                      <StopOutlined />
                                                    ) : (
                                                      <ClockCircleOutlined />
                                                    )
                                                  }
                                                  color={item.statusColor}
                                                >
                                                  {item.status}
                                                </Tag>{" "}
                                              </div>
                                            }
                                          </Col>
                                        </Row>
                                        <br />
                                        <br />
                                        <Row>
                                          <Col span={20} />
                                          <Col>
                                            <Button
                                              onClick={() =>
                                                handleClick(item.id)
                                              }
                                            >
                                              See details
                                            </Button>
                                          </Col>
                                        </Row>
                                      </div>
                                    }
                                  />
                                </List.Item>
                              )}
                            />
                          </div>
                          <Tabs type="card" style={{ marginTop: "30px" }}>
                            <TabPane tab="Resources" key="1">
                              <Table
                                dataSource={resources}
                                columns={resourceColumns}
                              />
                            </TabPane>
                            <TabPane tab="Outputs" key="2">
                              <Table
                                dataSource={outputs}
                                columns={outputColumns}
                              />
                            </TabPane>
                          </Tabs>
                        </div>
                      )}
                    </Col>
                    <Col span={5}>
                      <Space direction="vertical">
                        <br />
                        <span className="App-text">
                          { workspace.data.attributes.branch !== "remote-content" ? (
                            <>
                              {" "}
                              {renderVCSLogo(vcsProvider)}{" "}
                              <a
                                href={fixSshURL(
                                  workspace.data.attributes.source
                                )}
                                target="_blank"
                              >
                                {new URL(
                                  fixSshURL(workspace.data.attributes.source)
                                )?.pathname
                                  ?.replace(".git", "")
                                  ?.substring(1)}
                              </a>
                            </>
                          ) : (
                            <>
                              <IconContext.Provider value={{ size: "1.4em" }}>
                                <BiTerminal />
                              </IconContext.Provider>
                              &nbsp;&nbsp;cli/api driven workflow
                            </>
                          )}
                        </span>
                        <span className="App-text">
                          <ThunderboltOutlined /> Execution Mode:{" "}
                          <a onClick={handleClickSettings}>{executionMode}</a>{" "}
                        </span>
                        <span className="App-text">
                          <PlayCircleOutlined /> Auto apply: <a>Off</a>{" "}
                        </span>
                        <Divider />
                        <h4>Tags</h4>
                        <Tags
                          organizationId={organizationId}
                          workspaceId={id}
                        />
                      </Space>
                    </Col>
                  </Row>
                </TabPane>

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
                                  #job-{item.id} |
                                  {item.commitId !== "000000000" ? (
                                    <>
                                      {" "}
                                      commitId {item.commitId?.substring(
                                        0,
                                        6
                                      )}{" "}
                                    </>
                                  ) : (
                                    ""
                                  )}
                                  | <b>{item.createdBy}</b> triggered via UI
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
                          executionMode:
                            workspace.data.attributes.executionMode,
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

                        <Form.Item
                          name="executionMode"
                          label="Execution Mode"
                          extra="Use this option with terraform remote state/cloud block if you want to execute Terraform CLI remotely and just upload the state to Terrakube"
                        >
                          <Select
                            defaultValue={
                              workspace.data.attributes.executionMode
                            }
                            style={{ width: 250 }}
                          >
                            <Option key="remote">remote</Option>
                            <Option key="local">local</Option>
                          </Select>
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
                        onDelete(workspace);
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

const characters =
  "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

function generateRandomString(length) {
  let result = "";
  const charactersLength = characters.length;
  for (let i = 0; i < length; i++) {
    result += characters.charAt(Math.floor(Math.random() * charactersLength));
  }

  return result;
}

function setupWorkspaceIncludes(
  includes,
  setVariables,
  setJobs,
  setEnvVariables,
  setHistory,
  setSchedule,
  templates,
  setLastRun,
  setVCSProvider,
  setCurrentStateId,
  currentStateId,
  axiosInstance,
  setResources,
  setOutputs
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
        console.log(element);
        history.push({
          id: element.id,
          title: "Queue manually using Terraform",
          relativeDate: DateTime.fromISO(
            element.attributes.createdDate
          ).toRelative(),
          createdDate: element.attributes.createdDate,
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
      case include.VCS:
        setVCSProvider(element.attributes.vcsType);
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

  // set state data
  var lastState = history
    .sort((a, b) => a.jobReference - b.jobReference)
    .reverse()[0];
  // reload state only if there is a new version
  console.log('Get latest state')
  if (currentStateId !== lastState?.id) {
    loadState(lastState, axiosInstance, setOutputs, setResources);
  }
  setCurrentStateId(lastState?.id);
}

function loadState(state, axiosInstance, setOutputs, setResources) {
  axiosInstance.get(state.output).then((resp) => {
    var result = parseState(resp.data);
    console.log("result", result);
    setResources(result.resources);
    setOutputs(result.outputs);
  });
}

function parseState(state) {
  var resources = [];
  var outputs = [];
  console.log("Current state")
  console.log(state)
  if(state?.values?.outputs != null){
    for (const [key, value] of Object.entries(state?.values?.outputs)) {
      outputs.push({
        name: key,
        type: value.type,
        value: value.value,
      });
    }
  } else {
    console.log('State has no outputs')
  }

  // parse root module resources
  if(state?.values?.root_module?.resources != null){
    for (const [key, value] of Object.entries(
      state?.values?.root_module?.resources
    )) {
      resources.push({
        name: value.name,
        type: value.type,
        provider: value.provider_name,
        module: "root",
      });
    }
  } else {
    console.log('State has no resources')
  }
  
  return { resources: resources, outputs: outputs };
}

function fixSshURL(source) {
  if (source.startsWith("git@")) {
    return source.replace(":", "/").replace("git@", "https://");
  } else {
    return source;
  }
}
