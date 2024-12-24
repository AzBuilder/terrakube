import { React, useState, useEffect } from "react";
import {
  Button,
  List,
  Form,
  Modal,
  Space,
  Input,
  InputNumber,
  Typography,
  Alert,
  Row,
  Col,
  Card,
  Tag,
  Popconfirm,
} from "antd";
import "./UserSettings.css";
import { axiosClientAuth } from "../../config/axiosConfig";
import { useParams } from "react-router-dom";
import {
  InfoCircleOutlined,
  DeleteOutlined,
  ClockCircleOutlined,
  ExclamationCircleOutlined,
} from "@ant-design/icons";
export const Tokens = () => {
  const { orgid } = useParams();
  const [tokens, setTokens] = useState([]);
  const [token, setToken] = useState("");
  const [loading, setLoading] = useState(false);
  const [visible, setVisible] = useState(false);
  const [visibleToken, setVisibleToken] = useState(false);
  const [creating, setCreating] = useState(false);
  const [form] = Form.useForm();
  const onCancel = () => {
    setVisible(false);
  };
  const { DateTime } = require("luxon");
  const { Paragraph } = Typography;
  const onNew = () => {
    form.resetFields();
    setVisible(true);
  };

  const onCreate = (values) => {
    const body = {
      description: values.description,
      days: values.days,
    };
    console.log(body);

    axiosClientAuth
      .post(
        `${new URL(window._env_.REACT_APP_TERRAKUBE_API_URL).origin}/pat/v1`,
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
        loadTokens();
        setVisible(false);
        setVisibleToken(true);
        setCreating(false);
        form.resetFields();
      });
  };

  const onDelete = (id) => {
    console.log("deleted " + id);
    axiosClientAuth
      .delete(`${new URL(window._env_.REACT_APP_TERRAKUBE_API_URL).origin}/pat/v1/${id}`)
      .then((response) => {
        console.log(response);
        loadTokens();
      });
  };

  const loadTokens = () => {
    axiosClientAuth
      .get(`${new URL(window._env_.REACT_APP_TERRAKUBE_API_URL).origin}/pat/v1`)
      .then((response) => {
        console.log(response);
        setTokens(response.data);
        setLoading(false);
      });
  };
  useEffect(() => {
    setLoading(true);
    loadTokens();
  }, [orgid]);

  return (
    <div className="setting">
      <h1>Tokens</h1>
      <div className="App-text">
        Your API tokens can be used to access the Terrakube API and perform all
        the actions your user account is entitled to. For more information, see
        the Terrakube documentation.
      </div>
      <Button type="primary" onClick={onNew} htmlType="button">
        Create an API Token
      </Button>
      <br></br>

      <h3 style={{ marginTop: "30px" }}>Tokens</h3>
      {loading || !tokens ? (
        <p>Data loading...</p>
      ) : (
        <List
          itemLayout="horizontal"
          dataSource={tokens}
          split={false}
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
                              This operation is irreversible.<br/>
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
                        <Tag
                          icon={<ExclamationCircleOutlined />}
                          color="warning"
                        >
                          {" "}
                          <b>
                            Expires{" "}
                            {(item.days > 0) ? DateTime.fromISO(item.createdDate)
                              .plus({ days: item.days })
                              .toLocaleString(DateTime.DATETIME_MED): "Token without expiration date"}
                          </b>
                        </Tag>
                      </Col>
                    </Row>
                    <br />
                    <Row>
                      <Col span={20} style={{ color: "rgb(82, 87, 97)" }}>
                        <ClockCircleOutlined /> Created{" "}
                        <b>{DateTime.fromISO(item.createdDate).toRelative()}</b>{" "}
                        by user <b>{item.createdBy}</b>
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
        title={"Create API token"}
        okText="Create API token"
        onCancel={onCancel}
        cancelText="Cancel"
        okButtonProps={{ loading: creating }}
        onOk={() => {
          form
            .validateFields()
            .then((values) => {
              setCreating(true);
              onCreate(values);
            })
            .catch((info) => {
              console.log("Validate Failed:", info);
            });
        }}
      >
        <Space style={{ width: "100%" }} direction="vertical">
          <Form name="tokens" form={form} layout="vertical">
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
              <InputNumber min={0} />
            </Form.Item>
          </Form>
        </Space>
      </Modal>
      <Modal
        width="600px"
        open={visibleToken}
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
            Your new API token is displayed below. Treat this token like a
            password, as it can be used to access your account without a
            username, password, or two-factor authentication.
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
                This token <b>will not be displayed again</b>, so make sure to
                save it to a safe place.
              </span>
            }
            type="warning"
            showIcon
          />
        </Space>
      </Modal>
    </div>
  );
};
