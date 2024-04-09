import { React, useEffect, useState } from "react";
import axiosInstance from "../../config/axiosConfig";
import { useHistory } from "react-router-dom";
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
import { ResourceDrawer } from "../Workspaces/ResourceDrawer";
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
import { HiOutlineExternalLink } from "react-icons/hi";
import { IconContext } from "react-icons";
import { FiGitCommit } from "react-icons/fi";
import { BiTerminal } from "react-icons/bi";
import "./Workspaces.css";
import { getServiceIcon } from "./Icons.js";
const { TabPane } = Tabs;
const { Option } = Select;
const { Paragraph } = Typography;
const include = {
  VARIABLE: "variable",
  JOB: "job",
  HISTORY: "history",
  SCHEDULE: "schedule",
  VCS: "vcs",
  AGENT: "agent",
};
const { DateTime } = require("luxon");
const { Content } = Layout;
const iacTypes = [
  {
    id: "terraform",
    name: "Terraform",
    description:
      "Create an empty template. So you can define your template from scratch.",
    icon: (
      <IconContext.Provider value={{ size: "1.4em" }}>
        <SiTerraform />
      </IconContext.Provider>
    ),
  },
  {
    id: "tofu",
    name: "OpenTofu",
    icon: <img width="18px" src="/providers/opentofu.png" />,
  },
];
export const WorkspaceDetails = ({ selectedTab }) => {
  const browserHistory = useHistory();
  const { id, runid } = useParams();
  const organizationId = localStorage.getItem(ORGANIZATION_ARCHIVE);
  localStorage.setItem(WORKSPACE_ARCHIVE, id);
  const [workspace, setWorkspace] = useState({});
  const [variables, setVariables] = useState([]);
  const [history, setHistory] = useState([]);
  const [schedule, setSchedule] = useState([]);
  const [open, setOpen] = useState(false);
  const [resource, setResource] = useState({});
  const [envVariables, setEnvVariables] = useState([]);
  const [jobs, setJobs] = useState([]);
  const [stateDetailsVisible, setStateDetailsVisible] = useState(false);
  const [jobId, setJobId] = useState(0);
  const [loading, setLoading] = useState(false);
  const [jobVisible, setjobVisible] = useState(false);
  const [organizationName, setOrganizationName] = useState([]);
  const [workspaceName, setWorkspaceName] = useState("...");
  const [activeKey, setActiveKey] = useState(selectedTab !== null ? selectedTab : "1");
  const [terraformVersions, setTerraformVersions] = useState([]);
  const [waiting, setWaiting] = useState(false);
  const [templates, setTemplates] = useState([]);
  const [lastRun, setLastRun] = useState("");
  const [executionMode, setExecutionMode] = useState("...");
  const [agent, setAgent] = useState("...");
  const [sshKeys, setSSHKeys] = useState([]);
  const [agentList, setAgentList] = useState([]);
  const [orgTemplates, setOrgTemplates] = useState([]);
  const [vcsProvider, setVCSProvider] = useState("");
  const [resources, setResources] = useState([]);
  const [outputs, setOutputs] = useState([]);
  const [currentStateId, setCurrentStateId] = useState(0);
  const [selectedIac, setSelectedIac] = useState("");
  const handleClick = (id) => {
    changeJob(id);
  };
  const handleIacChange = (iac) => {
    setSelectedIac(iac);
    loadVersions(iac);
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
      sorter: (a, b) => a.type.localeCompare(b.type),
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
      render: (text, record) => (
        <Button onClick={() => showDrawer(record)} type="link">
          {text} &nbsp;
          <HiOutlineExternalLink />
        </Button>
      ),
    },
    {
      title: "Provider",
      dataIndex: "provider",
      key: "provider",
      sorter: (a, b) => a.provider.localeCompare(b.provider),
    },
    {
      title: "Type",
      dataIndex: "type",
      key: "type",
      onFilter: (value, record) => record.type.indexOf(value) === 0,
      sorter: (a, b) => a.type.localeCompare(b.type),
      render: (text, record) => (
        <>
          <Avatar
            shape="square"
            size="small"
            src={getServiceIcon(record.provider, record.type)}
          />{" "}
          &nbsp;{text}
        </>
      ),
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


  const getIaCIconById = (id) => {
    const item = iacTypes.find((iacType) => iacType.id === id);
    return item ? item.icon : null;
  };

  const loadSSHKeys = () => {
    axiosInstance.get(`organization/${organizationId}/ssh`).then((response) => {
      console.log(response.data.data);
      setSSHKeys(response.data.data);
    });
  };

  const loadAgentlist = () => {
    axiosInstance.get(`organization/${organizationId}/agent`).then((response) => {
      console.log(response.data.data);
      setAgentList(response.data.data);
    });
  };

  const loadOrgTemplates = () => {
    axiosInstance.get(`organization/${organizationId}/template`).then((response) => {
      console.log(response.data.data);
      setOrgTemplates(response.data.data);
    });
  };

  const showDrawer = (record) => {
    setOpen(true);
    setResource(record);
  };

  const switchKey = (key) => {
    setActiveKey(key);
    switch (key) {
      case "1":
        browserHistory.push(`/workspaces/${id}`);
        break;
      case "2":
        setjobVisible(false);
        browserHistory.push(`/workspaces/${id}/runs`);
        break;
      case "3":
        setStateDetailsVisible(false);
        browserHistory.push(`/workspaces/${id}/states`);
        break;
      case "4":
        browserHistory.push(`/workspaces/${id}/variables`);
        break;
      case "5":
        browserHistory.push(`/workspaces/${id}/schedules`);
        break;
      case "6":
        browserHistory.push(`/workspaces/${id}/settings`);
        break;  
      default:
        break;
    }
  };
  useEffect(() => {
    setLoading(true);
    loadWorkspace(true);
    setLoading(false);
    loadSSHKeys();
    loadAgentlist();
    loadOrgTemplates();
    const interval = setInterval(() => {
      loadWorkspace(false);
    }, 15000);
    return () => clearInterval(interval);
  }, [id]);

  const changeJob = (id) => {
    console.log(id);
    setJobId(id);
    setjobVisible(true);
    setActiveKey("2");
  };

  const loadVersions = (iacType)=>{
    const versionsApi = `${
      new URL(window._env_.REACT_APP_TERRAKUBE_API_URL).origin
    }/${iacType}/index.json`
    axiosInstance.get(versionsApi).then((resp) => {
      console.log(resp);
      const tfVersions = [];
      if(iacType ==="tofu")
      {
        resp.data.forEach(release => {
          if (!release.tag_name.includes("-"))  tfVersions.push(release.tag_name.replace("v",""));
        });

    }
    else{

      for (const version in resp.data.versions) {
        if (!version.includes("-")) tfVersions.push(version);
      }

    }
      setTerraformVersions(tfVersions.sort(compareVersions).reverse());
      console.log(tfVersions);
    });
  }

  const loadWorkspace = (_loadVersions) => {
    axiosInstance
      .get(`organization/${organizationId}/template`)
      .then((template) => {
        setTemplates(template.data.data);
        axiosInstance
          .get(
            `organization/${organizationId}/workspace/${id}?include=job,variable,history,schedule,vcs,agent`
          )
          .then((response) => {
            if(_loadVersions) loadVersions(response.data.data.attributes.iacType);
            setWorkspace(response.data);
            console.log(response.data);
            if (response.data.included) {
              setupWorkspaceIncludes(
                response.data,
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
                setOutputs,
                  setAgent,
              );
            }
            setOrganizationName(localStorage.getItem(ORGANIZATION_NAME));
            setWorkspaceName(response.data.data.attributes.name);
            setExecutionMode(response.data.data.attributes.executionMode);
            if(runid) changeJob(runid);  // if runid is provided, show the job details
          });
      });
  };

  const handleClickSettings = () => {
    switchKey("6");
  };

  const handleLockButton = (locked) => {
    const body = {
      data: {
        type: "workspace",
        id: id,
        attributes: {
          locked: !locked,
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
          loadWorkspace(true);
          var newstatus = locked ? "unlocked" : "locked";
          message.success("Workspace " + newstatus + " successfully");
        } else {
          var newstatus = locked ? "unlock" : "lock";
          message.error("Workspace " + newstatus + " failed");
        }
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
          executionMode: values.executionMode,
          moduleSshKey: values.moduleSshKey,
          terraformVersion: values.terraformVersion,
          iacType: values.iacType,
          branch: values.branch,
          defaultTemplate: values.defaultTemplate,
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

    var bodyAgent;
    console.log(`Using Agent: ${values.executorAgent}`)
    if (values.executorAgent === "default"){
      bodyAgent = {
        data:  null
      }
    } else {
      bodyAgent = {
        data: {
          type: "agent",
          id: values.executorAgent
        }
      }
    }
    console.log(bodyAgent);
    axiosInstance.patch(`/organization/${organizationId}/workspace/${id}/relationships/agent`, bodyAgent, {
      headers: {
        "Content-Type": "application/vnd.api+json",
      },
    }).then((response) => {
      console.log("Update Workspace agent successfully")
      console.log(response);
      if (response.status == "204") {
        console.log("Workspace agent updated successfully");

      } else {
        console.log("Workspace agent update failed");
      }
    })
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
          browserHistory.push(`/organizations/${organizationId}/workspaces`);
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
                  <Space direction="horizontal">
                    {getIaCIconById(workspace.data.attributes?.iacType)}
                    <span>     
                    {getIaCNameById(workspace.data.attributes?.iacType)}{" "}               <a
                      onClick={handleClickSettings}
                      className="workspace-button"
                      style={{ color: "#3b3d45" }}
                    >
                      v{workspace.data.attributes.terraformVersion}
                    </a></span>

                  </Space>

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
                defaultActiveKey={selectedTab}
                onTabClick={handleStatesClick}
                tabBarExtraContent={
                  <>
                    <Space direction="horizontal">
                      {" "}
                      <Button
                        type="default"
                        htmlType="button"
                        onClick={() =>
                          handleLockButton(workspace.data.attributes.locked)
                        }
                        icon={
                          workspace.data.attributes.locked ? (
                            <UnlockOutlined />
                          ) : (
                            <LockOutlined />
                          )
                        }
                      >
                        {workspace.data.attributes.locked ? "Unlock" : "Lock"}
                      </Button>
                      <CreateJob changeJob={changeJob} />
                    </Space>
                  </>
                }
                onChange={callback}
              >
                <TabPane tab="Overview" key="1">
                  <Row>
                    <Col span={19} style={{ paddingRight: "20px" }}>
                      {workspace.data.attributes.source === "empty" &&
                      workspace.data.attributes.branch === "remote-content" &&
                      workspace.data.relationships.history.data.length < 1 ? (
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
                                            <b>{item.via || "UI"}</b>{" "}
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
                            <TabPane
                              tab={<>Resources ({resources.length})</>}
                              key="1"
                            >
                              <Table
                                dataSource={resources}
                                columns={resourceColumns}
                              />
                            </TabPane>
                            <TabPane
                              tab={<>Outputs ({outputs.length})</>}
                              key="2"
                            >
                              <Table
                                dataSource={outputs}
                                columns={outputColumns}
                              />
                            </TabPane>
                          </Tabs>
                          <ResourceDrawer
                            resource={resource}
                            setOpen={setOpen}
                            open={open}
                          />
                        </div>
                      )}
                    </Col>
                    <Col span={5}>
                      <Space direction="vertical">
                        <br />
                        <span className="App-text">
                          {workspace.data.attributes.branch !==
                          "remote-content" ? (
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
                                  | <b>{item.createdBy}</b> triggered via{" "}
                                  {item.via || "UI"}
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
                          moduleSshKey: workspace.data.attributes.moduleSshKey,
                          executionMode:
                            workspace.data.attributes.executionMode,
                          iacType: workspace.data.attributes.iacType,
                          branch: workspace.data.attributes.branch,
                          defaultTemplate: workspace.data.attributes.defaultTemplate,
                          executorAgent: workspace.data.relationships.agent.data?.id == null ? "default": workspace.data.relationships.agent.data?.id ,
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
                          label={
                            getIaCNameById(selectedIac || workspace.data.attributes?.iacType) +
                            " Version"
                          }
                          extra={
                            "The version of " +
                            getIaCNameById(selectedIac || workspace.data.attributes?.iacType) +
                            " to use for this workspace. Upon creating this workspace, the latest version was selected and will be used until it is changed manually. It will not upgrade automatically."
                          }
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
                          label={
                            getIaCNameById(selectedIac || workspace.data.attributes?.iacType) +
                            " Working Directory"
                          }
                          extra={
                            "The directory that " +
                            getIaCNameById(selectedIac || workspace.data.attributes?.iacType) +
                            " will execute within. This defaults to the root of your repository and is typically set to a subdirectory matching the environment when multiple environments exist within the same repository."
                          }
                        >
                          <Input />
                        </Form.Item>
                        <Form.Item
                          name="branch"
                          label="Branch used in VCS connections"
                          extra="Don't update the value when using CLI Driven workflows"
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
                          name="iacType"
                          label="Select IaC type "
                          extra="IaC type when running the workspace (Example: terraform or tofu) "
                        >
                          <Select
                            defaultValue={workspace.data.attributes?.iacType}
                            style={{ width: 250 }}
                            onChange={handleIacChange}
                          >
                            {iacTypes.map(function (iacType, index) {
                                return <Option key={iacType.id}>{getIaCIconById(iacType.id)} {iacType.name} </Option>
                            })}
                          </Select>
                        </Form.Item>
                        <Form.Item
                          name="executionMode"
                          label="Execution Mode"
                          extra={
                            "Use this option with terraform remote state/cloud block if you want to execute " +
                            getIaCNameById(selectedIac || workspace.data.attributes?.iacType) +
                            " CLI remotely and just upload the state to Terrakube"
                          }
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
                        <Form.Item
                            name="defaultTemplate"
                            label="Default template when doing a git push to the repository"
                            extra="Default template when doing a git push to the repository"
                        >
                          <Select
                              defaultValue={
                                workspace.data.attributes.defaultTemplate
                              }
                              placeholder="select default template"
                              style={{ width: 250 }}
                          >
                            {orgTemplates.map(function (template, index) {
                              return (
                                  <Option key={template?.id}>
                                    {template?.attributes?.name}
                                  </Option>
                              );
                            })}
                          </Select>
                        </Form.Item>
                        <Form.Item
                          name="moduleSshKey"
                          label="Download modules SSH Keys"
                          extra="Use this option to add a SSH key to allow module downloads"
                        >
                          <Select
                            defaultValue={
                              workspace.data.attributes.moduleSshKey
                            }
                            placeholder="select SSH Key"
                            style={{ width: 250 }}
                          >
                            {sshKeys.map(function (sshKey, index) {
                              return (
                                <Option key={sshKey?.id}>
                                  {sshKey?.attributes?.name}
                                </Option>
                              );
                            })}
                          </Select>
                        </Form.Item>
                        <Form.Item
                            name="executorAgent"
                            label="Executor agent to run the job"
                            extra="Use this option to select which executor agent will run the job remotely"
                        >
                          <Select
                              defaultValue={
                                workspace.data.attributes.moduleSshKey
                              }
                              placeholder="select Job Agent"
                              style={{ width: 250 }}
                          >
                            {agentList.map(function (agentKey, index) {
                              return (
                                  <Option key={agentKey?.id}>
                                    {agentKey?.attributes?.name}
                                  </Option>
                              );
                            })}
                            <Option key="default">default</Option>
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
  data,
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
  setOutputs,
  setAgent,
) {
  let variables = [];
  let jobs = [];
  let envVariables = [];
  let history = [];
  let schedule = [];
  let includes = data.included;
  console.log(data.attributes?.iacType);

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
          title:
            "Queue manually using " +
            getIaCNameById(data?.data?.attributes?.iacType),
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
          title:
            "Queue manually using " +
            getIaCNameById(data?.data?.attributes?.iacType),
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
      case include.AGENT:
        setAgent(element.id)
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

  console.log(`Parsing state for workspace ${localStorage.getItem(WORKSPACE_ARCHIVE)} `);
  // set state data
  var lastState = history
    .sort((a, b) => a.jobReference - b.jobReference)
    .reverse()[0];
  // reload state only if there is a new version
  console.log("Get latest state");
  if (currentStateId !== lastState?.id) {
    loadState(lastState, axiosInstance, setOutputs, setResources, localStorage.getItem(WORKSPACE_ARCHIVE));
  }
  setCurrentStateId(lastState?.id);
}

function loadState(state, axiosInstance, setOutputs, setResources, workspaceId) {
  if (!state) {
    return;
  }

  var currentState;
  var organizationId = localStorage.getItem(ORGANIZATION_ARCHIVE);

  axiosInstance.get(state.output).then((resp) => {
    var result = parseState(resp.data);

    if(result.outputs.length < 1 && result.resources.length < 1){
      axiosInstance.get(`${new URL(window._env_.REACT_APP_TERRAKUBE_API_URL).origin}/tfstate/v1/organization/${organizationId}/workspace/${workspaceId}/state/terraform.tfstate`).then((currentStateData) => {
        console.log("Current State Data")
        console.log(currentStateData.data)
        currentState = currentStateData.data

        console.log("Parsing state using current state data instead of json representation")
        result = parseOldState(currentState)

        console.log("result parsing state", result);
        setResources(result.resources);
        setOutputs(result.outputs);
      }).catch(function (error) {console.error(error)});

    } else {
      console.log("result parsing state", result);
      setResources(result.resources);
      setOutputs(result.outputs);
    }
  });
}

function parseState(state) {
  var resources = [];
  var outputs = [];
  console.log("Current state");
  console.log(state);
  // parse root outputs
  if (state?.values?.outputs != null) {
    for (const [key, value] of Object.entries(state?.values?.outputs)) {
      if (typeof value.type === "string") {
        console.log(typeof value.type);
        outputs.push({
          name: key,
          type: value.type,
          value: value.value,
        });
      } else {
        console.log(typeof value.type);
        const jsonObject = JSON.stringify(value.value);
        const jsonType = value.type.toString();
        console.log(jsonObject);
        outputs.push({
          name: key,
          type: "Other type",
          value: jsonObject,
        });
      }
    }
  } else {
    console.log("State has no outputs");
  }

  // parse root module resources
  if (state?.values?.root_module?.resources != null) {
    for (const [key, value] of Object.entries(
      state?.values?.root_module?.resources
    )) {
      resources.push({
        name: value.name,
        type: value.type,
        provider: value.provider_name,
        module: "root_module",
        values: value.values,
        depends_on: value.depends_on,
      });
    }
  } else {
    console.log("State has no resources");
  }

  // parse child module resources
  if (state?.values?.root_module?.child_modules?.length > 0) {
    state?.values?.root_module?.child_modules?.forEach((moduleVal, index) => {
      console.log(`Checking child ${moduleVal.address} with index ${index}`);
      if(moduleVal.resources != null)
        for (const [key, value] of Object.entries(moduleVal.resources)) {
          resources.push({
            name: value.name,
            type: value.type,
            provider: value.provider_name,
            module: moduleVal.address,
            values: value.values,
            depends_on: value.depends_on,
          });
        }

      if(moduleVal.child_modules?.length > 0) {
        resources = parseChildModules(resources , moduleVal.child_modules);
      }
    });
  } else {
    console.log("State has no child modules resources");
  }

  return { resources: resources, outputs: outputs };
}

function parseOldState(state) {
  var resources = [];
  var outputs = [];
  console.log("Current State Data using fallback parsing method");
  console.log(state);

  console.log("Parsing outputs fallback method");
  if (state?.outputs != null) {
    for (const [key, value] of Object.entries(state?.outputs)) {
      if (typeof value.type === "string") {
        console.log(typeof value.type);
        outputs.push({
          name: key,
          type: value.type,
          value: value.value,
        });
      } else {
        console.log(typeof value.type);
        const jsonObject = JSON.stringify(value.value);
        console.log(jsonObject);
        outputs.push({
          name: key,
          type: "Other type",
          value: jsonObject,
        });
      }
    }
  } else {
    console.log("State has no outputs");
  }

  console.log("Parsing resources and modules fallback method");
  if (state?.resources != null && state?.resources.length > 0) {
    state?.resources.forEach((value) => {
      if(value.module != null) {
        resources.push({
          name: value.name,
          type: value.type,
          provider: value.provider.replace("provider[","").replace("]",""),
          module: value.module,
          values: value.instances[0].attributes,
          depends_on: value.instances[0].dependencies,
        });
      } else {
        resources.push({
          name: value.name,
          type: value.type,
          provider: value.provider.replace("provider[\"","").replace("\"]",""),
          module: "root_module",
          values: value.instances[0].attributes,
          depends_on: value.instances[0].dependencies,
        });
      }

    });

  } else {
    console.log("State has no resources/modules");
  }

  console.log({ resources: resources, outputs: outputs })

  return { resources: resources, outputs: outputs };
}

function parseChildModules(resources, child_modules){
  child_modules?.forEach((moduleVal, index) => {
    console.log(`Checking nested child ${moduleVal.address} with index ${index}`);
    if(moduleVal.resources != null)
      for (const [key, value] of Object.entries(moduleVal.resources)) {
        resources.push({
          name: value.name,
          type: value.type,
          provider: value.provider_name,
          module: moduleVal.address,
          values: value.values,
          depends_on: value.depends_on,
        });
      }

    if(moduleVal.child_modules?.length > 0) {
      resources = parseChildModules(resources);
    }
  });

  return resources;
}

function fixSshURL(source) {
  if (source.startsWith("git@")) {
    return source.replace(":", "/").replace("git@", "https://");
  } else {
    return source;
  }
}

function getIaCNameById(id) {
  const item = iacTypes.find((iacType) => iacType.id === id);
  return item ? item.name : null;
};