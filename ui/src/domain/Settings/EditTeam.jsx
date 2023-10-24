import { React, useState, useEffect } from "react";
import "./Settings.css";
import {
  Space,
  Switch,
  Form,
  Input,
  Button,
  List,
  Modal,
  InputNumber,
  Typography,
  Alert,
  Avatar,
  Tooltip,
} from "antd";
import axiosInstance, { axiosClientAuth } from "../../config/axiosConfig";
import { useParams } from "react-router-dom";
import { InfoCircleOutlined, LockOutlined } from "@ant-design/icons";
import FormItem from "antd/es/form/FormItem";
const { DateTime } = require("luxon");
const { Paragraph } = Typography;

export const EditTeam = ({ mode, setMode, teamId, loadTeams }) => {
  const { orgid } = useParams();
  const [loading, setLoading] = useState(true);
  const [loadingTokens, setLoadingTokens] = useState(true);
  const [form] = Form.useForm();
  const [formToken] = Form.useForm();
  const [teamName, setTeamName] = useState(false);
  const [tokens, setTokens] = useState([]);
  const [visible, setVisible] = useState(false);
  const [visibleToken, setVisibleToken] = useState(false);
  const [creating, setCreating] = useState(false);
  const [token, setToken] = useState("");
  const [createTokenDisabled, setCreateTokenDisabled] = useState(true);
  useEffect(() => {
    if (mode === "edit") {
      setLoading(true);
      setLoadingTokens(true);
      loadTeam(teamId);
    } else {
      form.resetFields();
      setLoading(false);
    }
  }, [teamId]);

  const loadTeam = (id) => {
    axiosInstance.get(`organization/${orgid}/team/${id}`).then((response) => {
      setTeamName(response.data.data.attributes.name);
      form.setFieldsValue({
        manageProvider: response.data.data.attributes.manageProvider,
        manageModule: response.data.data.attributes.manageModule,
        manageWorkspace: response.data.data.attributes.manageWorkspace,
        manageVcs: response.data.data.attributes.manageVcs,
        manageTemplate: response.data.data.attributes.manageTemplate,
      });
      setLoading(false);
      loadTokens(response.data.data.attributes.name);
      loadUserTeams(response.data.data.attributes.name);
    });
  };

  const onCreate = (values) => {
    const body = {
      data: {
        type: "team",
        attributes: {
          name: values.name,
          manageWorkspace: values.manageWorkspace,
          manageModule: values.manageModule,
          manageProvider: values.manageProvider,
          manageVcs: values.manageVcs,
          manageTemplate: values.manageTemplate,
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

  const onUpdate = (values) => {
    const body = {
      data: {
        type: "team",
        id: teamId,
        attributes: {
          manageWorkspace: values.manageWorkspace,
          manageModule: values.manageModule,
          manageProvider: values.manageProvider,
          manageVcs: values.manageVcs,
          manageTemplate: values.manageTemplate,
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

  const onFinish = (values) => {
    if (mode === "edit") {
      onUpdate(values);
    } else {
      onCreate(values);
    }
  };

  const onCancel = () => {
    setMode("list");
    form.resetFields();
  };

  const onCancelToken = () => {
    setVisible(false);
    formToken.resetFields();
  };

  const onCreateToken = (values) => {
    const body = {
      description: values.description,
      days: values.days,
      group: teamName,
    };
    console.log(body);

    axiosClientAuth
      .post(
        `${
          new URL(window._env_.REACT_APP_TERRAKUBE_API_URL).origin
        }/access-token/v1/teams`,
        body,
        {
          headers: {
            "Content-Type": "application/vnd.api+json",
          },
        }
      )
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

  const loadTokens = (tokenName) => {
    axiosClientAuth
      .get(
        `${
          new URL(window._env_.REACT_APP_TERRAKUBE_API_URL).origin
        }/access-token/v1/teams`
      )
      .then((response) => {
        console.log(response);
        var filteredTokens = response.data.filter(
          (token) => token.group === tokenName
        );
        setTokens(filteredTokens);
        setLoadingTokens(false);
      });
  };

  const loadUserTeams = (teamName) => {
    axiosClientAuth
      .get(
        `${
          new URL(window._env_.REACT_APP_TERRAKUBE_API_URL).origin
        }/access-token/v1/teams/current-teams`
      )
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
              name="manageWorkspace"
              valuePropName="checked"
              label="Manage Workspaces"
              tooltip={{
                title:
                  "Allow members to create and administrate all workspaces within the organization",
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
                title:
                  "Allow members to create and administrate all modules within the organization",
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
                title:
                  "Allow members to create and administrate all providers within the organization",
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
                title:
                  "Allow members to create and administrate all VCS Settings within the organization",
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
                title:
                  "Allow members to create and administrate all Templates within the organization",
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
                {mode === "edit"
                  ? "Update team organization access"
                  : "Create team"}
              </Button>
            </Space>
          </Form>
        )}
      </Space>

      {mode === "edit" ? (
        <>
          <h2 style={{ marginTop: "30px" }}>Team API Tokens</h2>
          <div className="App-text">
            You can use team API tokens to perform API actions. The token’s
            access level matches the team’s access level. For example, if a team
            can execute jobs on workspaces, the token can also create jobs on
            workspaces through the API.
          </div>
          <Tooltip title="A team token can only be generated if you are member of this team.">
          <Button
            type="primary"
            disabled={createTokenDisabled}
            onClick={onNewToken}
            htmlType="button"
            to
          >
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
                    title={item.description}
                    avatar={
                      <Avatar
                        style={{ backgroundColor: "#1890ff" }}
                        icon={<LockOutlined />}
                      ></Avatar>
                    }
                    description={
                      <span>
                        Created{" "}
                        <b>{DateTime.fromISO(item.createdDate).toRelative()}</b>{" "}
                        by user <b>{item.createdBy}</b> and expires{" "}
                        <b>
                          {DateTime.fromISO(item.createdDate)
                            .plus({ days: item.days })
                            .toLocaleString(DateTime.DATETIME_MED)}
                        </b>
                      </span>
                    }
                  />
                </List.Item>
              )}
            />
          )}
          <Modal
            width="600px"
            visible={visible}
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
              <Form name="tokens" form={formToken} layout="vertical">
                <Form.Item
                  name="description"
                  tooltip={{
                    title:
                      "Choose a description to help you identify this token later",
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
                  <InputNumber min={1} />
                </Form.Item>
              </Form>
            </Space>
          </Modal>
          <Modal
            width="600px"
            visible={visibleToken}
            title={"Create API token"}
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
                Your new Team API token is displayed below. Treat this token
                like a password, as it can be used to access your account
                without a username, password, or two-factor authentication.
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
                    This token <b>will not be displayed again</b>, so make sure
                    to save it to a safe place.
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
