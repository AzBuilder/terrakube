import { React, useState, useEffect } from "react";
import {
  Button,
  List,
  Popconfirm,
  Form,
  Modal,
  Space,
  Input,
  Select,
} from "antd";
import "./Settings.css";
import axiosInstance from "../../config/axiosConfig";
import { useParams } from "react-router-dom";
import { EditOutlined, DeleteOutlined } from "@ant-design/icons";
const { TextArea } = Input;
export const SSHKeysSettings = () => {
  const { orgid } = useParams();
  const [sshKeys, setSSHKeys] = useState([]);
  const [loading, setLoading] = useState(false);
  const [visible, setVisible] = useState(false);
  const [sshKeyName, setSSHKeyName] = useState(false);
  const [mode, setMode] = useState("create");
  const [sshKeyId, setSSHKeyId] = useState([]);
  const [form] = Form.useForm();
  const onCancel = () => {
    setVisible(false);
  };

  const onNew = () => {
    form.resetFields();
    setVisible(true);
    setSSHKeyName("");
    setMode("create");
  };

  const onDelete = (id) => {
    console.log("deleted " + id);
    axiosInstance.delete(`organization/${orgid}/ssh/${id}`).then((response) => {
      console.log(response);
      loadSSHKeys();
    });
  };

  const onCreate = (values) => {
    const body = {
      data: {
        type: "ssh",
        attributes: {
          name: values.name,
          description: values.description,
          sshType: values.sshType,
          privateKey: values.privateKey,
        },
      },
    };
    console.log(body);

    axiosInstance
      .post(`organization/${orgid}/ssh`, body, {
        headers: {
          "Content-Type": "application/vnd.api+json",
        },
      })
      .then((response) => {
        console.log(response);
        loadSSHKeys();
        setVisible(false);
        form.resetFields();
      });
  };

  const onUpdate = (values) => {
    const body = {
      data: {
        type: "ssh",
        id: sshKeyId,
        attributes: {
          description: values.description,
          sshType: values.sshType,
          privateKey: values.privateKey,
        },
      },
    };
    console.log(body);

    axiosInstance
      .patch(`organization/${orgid}/ssh/${sshKeyId}`, body, {
        headers: {
          "Content-Type": "application/vnd.api+json",
        },
      })
      .then((response) => {
        console.log(response);
        loadSSHKeys();
        setVisible(false);
        form.resetFields();
      });
  };

  const loadSSHKeys = () => {
    axiosInstance.get(`organization/${orgid}/ssh`).then((response) => {
      console.log(response);
      setSSHKeys(response.data);
      setLoading(false);
    });
  };
  useEffect(() => {
    setLoading(true);
    loadSSHKeys();
  }, [orgid]);

  return (
    <div className="setting">
      <h1>SSH Keys</h1>
      <div className="App-text">
        Terrakube uses these private SSH keys for downloading private Terraform
        modules with Git-based sources during a Terraform run. SSH keys for
        downloading modules are assigned per-workspace.
      </div>
      <Button type="primary" onClick={onNew} htmlType="button">
        Add a Private SSH Key
      </Button>
      <br></br>

      <h3 style={{ marginTop: "30px" }}>SSH Keys</h3>
      {loading || !sshKeys.data ? (
        <p>Data loading...</p>
      ) : (
        <List
          itemLayout="horizontal"
          dataSource={sshKeys.data}
          renderItem={(item) => (
            <List.Item
              actions={[
                <Popconfirm
                  onConfirm={() => {
                    onDelete(item.id);
                  }}
                  style={{ width: "20px" }}
                  title={
                    <p>
                      This will permanently delete this SSH Key <br />
                      Any workspaces configured with this SSH key will no longer
                      use it to download Terraform modules. <br />
                      Are you sure?
                    </p>
                  }
                  okText="Yes"
                  cancelText="No"
                >
                  {" "}
                  <Button icon={<DeleteOutlined />} type="link" danger>
                    Delete
                  </Button>
                </Popconfirm>,
              ]}
            >
              <List.Item.Meta
                description={item.attributes.description}
                title={item.attributes.name}
              />
            </List.Item>
          )}
        />
      )}

      <Modal
        width="650px"
        visible={visible}
        title={
          mode === "edit"
            ? "Edit Private SSH Key " + sshKeyName
            : "Add a new Private SSH Key"
        }
        okText="Save SSH Key"
        onCancel={onCancel}
        cancelText="Cancel"
        onOk={() => {
          form
            .validateFields()
            .then((values) => {
              if (mode === "create") onCreate(values);
              else onUpdate(values);
            })
            .catch((info) => {
              console.log("Validate Failed:", info);
            });
        }}
      >
        <Space style={{ width: "100%" }} direction="vertical">
          <Form name="sshKey" form={form} layout="vertical">
            {mode === "create" ? (
              <Form.Item name="name" label="Name" rules={[{ required: true }]}>
                <Input />
              </Form.Item>
            ) : (
              ""
            )}

            <Form.Item
              name="description"
              label="Description"
              rules={[{ required: true }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="sshType"
              label="SSH Type"
              rules={[{ required: true }]}
            >
              <Select placeholder="Please select a ssh type">
                <Select.Option value="rsa">RSA</Select.Option>
                <Select.Option value="ed25519">ED25519</Select.Option>
              </Select>
            </Form.Item>
            <Form.Item
              name="privateKey"
              rules={[{ required: true }]}
              label="Private SSH Key"
              extra={
                <p>
                  Generate a new key with{" "}
                  <code style={{ backgroundColor: "#ebeef2" }}>
                    ssh-keygen -t rsa -m PEM
                  </code>
                  , and paste the private key. The contents should begin with{" "}
                  <code style={{ backgroundColor: "#ebeef2" }}>
                    -----BEGIN RSA PRIVATE KEY-----
                  </code>
                </p>
              }
            >
              <TextArea rows={6} />
            </Form.Item>
          </Form>
        </Space>
      </Modal>
    </div>
  );
};
