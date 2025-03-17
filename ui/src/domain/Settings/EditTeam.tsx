import { ClockCircleOutlined, DeleteOutlined, ExclamationCircleOutlined, InfoCircleOutlined } from "@ant-design/icons";
import {
  Alert,
  Button,
  Card,
  Col,
  Form,
  Input,
  InputNumber,
  List,
  Modal,
  Popconfirm,
  Row,
  Space,
  Switch,
  Tag,
  Tooltip,
  Typography,
} from "antd";
import FormItem from "antd/es/form/FormItem";
import { DateTime } from "luxon";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axiosInstance, { axiosClientAuth } from "../../config/axiosConfig";
import { TeamToken } from "../types";
import "./Settings.css";
const { Paragraph } = Typography;

type Props = {
  mode: "edit" | "create";
  setMode: React.Dispatch<React.SetStateAction<"list" | "edit" | "create">>;
  teamId?: string;
  loadTeams: () => void;
};

type CreateTeamForm = {
  name: string;
} & UpdatTeamForm;

type UpdatTeamForm = {
  manageCollection: boolean;
  manageJob: boolean;
  manageModule: boolean;
  manageProvider: boolean;
  manageState: boolean;
  manageTemplate: boolean;
  manageVcs: boolean;
  manageWorkspace: boolean;
};

type TokenForm = {
  description: string;
  days: number;
  minutes: number;
  hours: number;
};

export const EditTeam = ({ mode, setMode, teamId, loadTeams }: Props) => {
  const { orgid } = useParams();
  const [loading, setLoading] = useState(true);
  const [loadingTokens, setLoadingTokens] = useState(true);
  const [form] = Form.useForm();
  const [formToken] = Form.useForm();
  const [teamName, setTeamName] = useState<string>();
  const [tokens, setTokens] = useState<TeamToken[]>([]);
  const [visible, setVisible] = useState(false);
  const [visibleToken, setVisibleToken] = useState(false);
  const [creating, setCreating] = useState(false);
  const [token, setToken] = useState("");
  const [createTokenDisabled, setCreateTokenDisabled] = useState(true);
  useEffect(() => {
    if (mode === "edit" && teamId) {
      setLoading(true);
      setLoadingTokens(true);
      loadTeam(teamId);
    } else {
      form.resetFields();
      setLoading(false);
    }
  }, [teamId]);

  const loadTeam = (id: string) => {
    axiosInstance.get(`organization/${orgid}/team/${id}`).then((response) => {
      setTeamName(response.data.data.attributes.name);
      form.setFieldsValue({
        manageState: response.data.data.attributes.manageState,
        manageProvider: response.data.data.attributes.manageProvider,
        manageModule: response.data.data.attributes.manageModule,
        manageWorkspace: response.data.data.attributes.manageWorkspace,
        manageVcs: response.data.data.attributes.manageVcs,
        manageTemplate: response.data.data.attributes.manageTemplate,
        manageCollection: response.data.data.attributes.manageCollection,
        manageJob: response.data.data.attributes.manageJob,
      });
      setLoading(false);
      loadTokens(response.data.data.attributes.name);
      loadUserTeams(response.data.data.attributes.name);
    });
  };

  const onCreate = (values: CreateTeamForm) => {
    const body = {
      data: {
        type: "team",
        attributes: {
          name: values.name,
          manageState: values.manageState,
          manageWorkspace: values.manageWorkspace,
          manageModule: values.manageModule,
          manageProvider: values.manageProvider,
          manageVcs: values.manageVcs,
          manageTemplate: values.manageTemplate,
          manageCollection: values.manageCollection,
          manageJob: values.manageJob,
        },
      },
    };
    console.log(body);

    axiosInstance
      .post(`organization/${orgid}/team`, body, {
        headers: {
          "Content-Type": "application/vnd.api+json",
        },
      })
      .then((response) => {
        console.log(response);
        loadTeams();
        loadTokens();
        setMode("list");
        form.resetFields();
      });
  };

  const onNewToken = () => {
    formToken.resetFields();
    setVisible(true);
  };

  const onUpdate = (values: UpdatTeamForm) => {
    const body = {
      data: {
        type: "team",
        id: teamId,
        attributes: {
          manageState: values.manageState,
          manageWorkspace: values.manageWorkspace,
          manageModule: values.manageModule,
          manageProvider: values.manageProvider,
          manageVcs: values.manageVcs,
          manageTemplate: values.manageTemplate,
          manageCollection: values.manageCollection,
          manageJob: values.manageJob,
        },
      },
    };
    console.log(body);

    axiosInstance
      .patch(`organization/${orgid}/team/${teamId}`, body, {
        headers: {
          "Content-Type": "application/vnd.api+json",
        },
      })
      .then((response) => {
        console.log(response);
        loadTeams();
        setMode("list");
        form.resetFields();
      });
  };

  const onFinish = (values: CreateTeamForm | UpdatTeamForm) => {
    if (mode === "edit") {
      onUpdate(values);
    } else {
      onCreate(values as CreateTeamForm);
    }
  };

  const onDelete = (id: string) => {
    console.log("deleted " + id);
    axiosClientAuth
      .delete(`${new URL(window._env_.REACT_APP_TERRAKUBE_API_URL).origin}/access-token/v1/teams/${id}`)
      .then((response) => {
        console.log(response);
        loadTokens();
      });
  };

  const onCancel = () => {
    setMode("list");
    form.resetFields();
  };

  const onCancelToken = () => {
    setVisible(false);
    formToken.resetFields();
  };

  const onCreateToken = (values: TokenForm) => {
    const body = {
      description: values.description,
      days: values.days,
      minutes: values.minutes,
      hours: values.hours,
      group: teamName,
    };
    console.log(body);

    axiosClientAuth
      .post(`${new URL(window._env_.REACT_APP_TERRAKUBE_API_URL).origin}/access-token/v1/teams`, body, {
        headers: {
          "Content-Type": "application/vnd.api+json",
        },
      })
      .then((response) => {
        console.log(response);
        setToken(response.data.token);
        loadTokens(teamName);
        setVisible(false);
        setVisibleToken(true);
        setCreating(false);
        formToken.resetFields();
      });
  };

  const loadTokens = (tokenName?: string) => {
    axiosClientAuth
      .get(`${new URL(window._env_.REACT_APP_TERRAKUBE_API_URL).origin}/access-token/v1/teams`)
      .then((response) => {
        console.log(response);
        var filteredTokens = response.data.filter((token: any) => token.group === tokenName);
        setTokens(filteredTokens);
        setLoadingTokens(false);
      });
  };

  const loadUserTeams = (teamName: string) => {
    axiosClientAuth
      .get(`${new URL(window._env_.REACT_APP_TERRAKUBE_API_URL).origin}/access-token/v1/teams/current-teams`)
      .then((response) => {
        console.log(response);
        if (response.data?.groups.includes(teamName)) {
          setCreateTokenDisabled(false);
        }
      });
  };

  return (
    <div>
      <h1>{mode === "edit" ? <>Team: {teamName}</> : "New team"} </h1>
      <Space className="chooseType" direction="vertical">
        {loading ? (
          <p>Data loading...</p>
        ) : (
          <Form name="team" form={form} onFinish={onFinish} layout="vertical">
            {mode === "create" ? (
              <Form.Item
                name="name"
                tooltip={{
                  title: "Must be a valid AD Group name",
                  icon: <InfoCircleOutlined />,
                }}
                label="Name"
                rules={[{ required: true }]}
              >
                <Input />
              </Form.Item>
            ) : (
              ""
            )}
            <FormItem>
              <h2 style={{ marginTop: "10px" }}>Organization Access</h2>
            </FormItem>
            <Form.Item
              name="manageState"
              valuePropName="checked"
              label="Manage State"
              tooltip={{
                title:
                  "Allow members to manage Terraform/OpenTofu state, include downloading, uploading and view state content of a workspace.",
                icon: <InfoCircleOutlined />,
              }}
            >
              <Switch />
            </Form.Item>
            <Form.Item
              name="manageWorkspace"
              valuePropName="checked"
              label="Manage Workspaces"
              tooltip={{
                title: "Allow members to create and administrate all workspaces within the organization",
                icon: <InfoCircleOutlined />,
              }}
            >
              <Switch />
            </Form.Item>
            <Form.Item
              name="manageModule"
              valuePropName="checked"
              label="Manage Modules"
              tooltip={{
                title: "Allow members to create and administrate all modules within the organization",
                icon: <InfoCircleOutlined />,
              }}
            >
              <Switch />
            </Form.Item>
            <Form.Item
              name="manageCollection"
              valuePropName="checked"
              label="Manage Collections"
              tooltip={{
                title: "Allow members to create and manage all collections within the organization",
                icon: <InfoCircleOutlined />,
              }}
            >
              <Switch />
            </Form.Item>
            <Form.Item
              name="manageJob"
              valuePropName="checked"
              label="Manage Job"
              tooltip={{
                title: "Allow members to create jobs inside the organization",
                icon: <InfoCircleOutlined />,
              }}
            >
              <Switch />
            </Form.Item>
            <Form.Item
              name="manageProvider"
              valuePropName="checked"
              label="Manage Providers"
              tooltip={{
                title: "Allow members to create and administrate all providers within the organization",
                icon: <InfoCircleOutlined />,
              }}
            >
              <Switch />
            </Form.Item>
            <Form.Item
              name="manageVcs"
              valuePropName="checked"
              label="Manage VCS Settings"
              tooltip={{
                title: "Allow members to create and administrate all VCS Settings within the organization",
                icon: <InfoCircleOutlined />,
              }}
            >
              <Switch />
            </Form.Item>
            <Form.Item
              name="manageTemplate"
              valuePropName="checked"
              label="Manage Templates"
              tooltip={{
                title: "Allow members to create and administrate all Templates within the organization",
                icon: <InfoCircleOutlined />,
              }}
            >
              <Switch />
            </Form.Item>
            <Space direction="horizontal">
              {mode === "create" ? (
                <Button onClick={onCancel} type="default">
                  Cancel
                </Button>
              ) : (
                <></>
              )}
              <Button type="primary" htmlType="submit">
                {mode === "edit" ? "Update team organization access" : "Create team"}
              </Button>
            </Space>
          </Form>
        )}
      </Space>

      {mode === "edit" ? (
        <>
          <h2 style={{ marginTop: "30px" }}>Team API Tokens</h2>
          <div className="App-text">
            You can use team API tokens to perform API actions. The token’s access level matches the team’s access
            level. For example, if a team can execute jobs on workspaces, the token can also create jobs on workspaces
            through the API.
          </div>
          <Tooltip title="A team token can only be generated if you are member of this team.">
            <Button type="primary" disabled={createTokenDisabled} onClick={onNewToken} htmlType="button">
              Create a Team Token
            </Button>
          </Tooltip>
          <h4 style={{ marginTop: "30px" }}>Team API Tokens List</h4>
          {loadingTokens ? (
            <p>Data loading...</p>
          ) : (
            <List
              itemLayout="horizontal"
              dataSource={tokens}
              renderItem={(item) => (
                <List.Item>
                  <List.Item.Meta
                    description={
                      <Card style={{ width: "100%" }}>
                        <Row>
                          <Col span={23}>
                            <h3>{item.description}</h3>
                          </Col>
                          <Col span={1}>
                            <Popconfirm
                              onConfirm={() => {
                                onDelete(item.id);
                              }}
                              style={{ width: "20px" }}
                              title={
                                <p>
                                  This operation is irreversible.
                                  <br />
                                  Are you sure you want to proceed? <br />
                                </p>
                              }
                              okText="Yes"
                              cancelText="No"
                            >
                              <Button icon={<DeleteOutlined />}></Button>
                            </Popconfirm>
                          </Col>
                        </Row>
                        <Row>
                          <Col span={20}>
                            <Tag icon={<ExclamationCircleOutlined />} color="warning">
                              {" "}
                              <b>
                                Expires{": "}
                                {item.days > 0 || item.minutes > 0 || item.hours > 0
                                  ? DateTime.fromISO(item.createdDate)
                                      .plus({ days: item.days, minutes: item.minutes, hours: item.hours })
                                      .toLocaleString(DateTime.DATETIME_MED)
                                  : "Token without expiration date"}
                              </b>
                            </Tag>
                          </Col>
                        </Row>
                        <br />
                        <Row>
                          <Col span={20} style={{ color: "rgb(82, 87, 97)" }}>
                            <ClockCircleOutlined /> Created <b>{DateTime.fromISO(item.createdDate).toRelative()}</b> by
                            user <b>{item.createdBy}</b>
                          </Col>
                        </Row>
                      </Card>
                    }
                  />
                </List.Item>
              )}
            />
          )}
          <Modal
            width="600px"
            open={visible}
            title={"Creating a team token"}
            okText="Generate token"
            onCancel={onCancelToken}
            cancelText="Cancel"
            okButtonProps={{ loading: creating }}
            onOk={() => {
              formToken
                .validateFields()
                .then((values) => {
                  setCreating(true);
                  console.log(values);
                  onCreateToken(values);
                })
                .catch((info) => {
                  console.log("Validate Failed:", info);
                });
            }}
          >
            <Space style={{ width: "100%" }} direction="vertical">
              <Form name="tokens" initialValues={{ minutes: 0, hours: 0, days: 0 }} form={formToken} layout="vertical">
                <Form.Item
                  name="description"
                  tooltip={{
                    title: "Choose a description to help you identify this token later",
                    icon: <InfoCircleOutlined />,
                  }}
                  label="Description"
                  rules={[{ required: true }]}
                >
                  <Input />
                </Form.Item>
                <Form.Item
                  name="days"
                  tooltip={{
                    title: "Number of days for the token to be valid",
                    icon: <InfoCircleOutlined />,
                  }}
                  label="Days"
                  rules={[{ required: true }]}
                >
                  <InputNumber min={0} />
                </Form.Item>
                <Form.Item
                  name="hours"
                  tooltip={{
                    title: "Number of hours for the token to be valid",
                    icon: <InfoCircleOutlined />,
                  }}
                  label="Hours"
                  rules={[{ required: true }]}
                >
                  <InputNumber min={0} />
                </Form.Item>
                <Form.Item
                  name="minutes"
                  tooltip={{
                    title: "Number of minutes for the token to be valid",
                    icon: <InfoCircleOutlined />,
                  }}
                  label="Minutes"
                  rules={[{ required: true }]}
                >
                  <InputNumber min={0} />
                </Form.Item>
              </Form>
            </Space>
          </Modal>
          <Modal
            width="600px"
            visible={visibleToken}
            title={"Create Team API token"}
            okText="Done"
            onOk={() => {
              setVisibleToken(false);
            }}
            onCancel={() => {
              setVisibleToken(false);
            }}
          >
            <Space style={{ width: "100%" }} direction="vertical">
              <p>
                Your new Team API token is displayed below. Treat this token like a password, as it can be used to
                access your account without a username, password, or two-factor authentication.
              </p>
              <p>
                <Paragraph style={{ backgroundColor: "#ebeef2" }} copyable>
                  {token}
                </Paragraph>
              </p>
              <Alert
                message="Warning"
                description={
                  <span>
                    This token <b>will not be displayed again</b>, so make sure to save it to a safe place.
                  </span>
                }
                type="warning"
                showIcon
              />
            </Space>
          </Modal>
        </>
      ) : (
        ""
      )}

      {mode === "edit" ? (
        <Button style={{ marginTop: "60px" }} onClick={onCancel} type="default">
          Go back to Teams list
        </Button>
      ) : (
        <></>
      )}
    </div>
  );
};
