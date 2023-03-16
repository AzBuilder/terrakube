import { React, useState, useEffect } from "react";
import {
  Button,
  List,
  Popconfirm,
  Form,
  Modal,
  Space,
  Input,
  Avatar,
} from "antd";
import "./Settings.css";
import { Buffer } from "buffer";
import axiosInstance from "../../config/axiosConfig";
import { useParams } from "react-router-dom";
import {
  InfoCircleOutlined,
  LayoutOutlined,
  EditOutlined,
  DeleteOutlined,
} from "@ant-design/icons";

const { TextArea } = Input;

export const PortalSettings = () => {
  const { orgid } = useParams();
  const [portals, setPortals] = useState([]);
  const [loading, setLoading] = useState(false);
  const [visible, setVisible] = useState(false);
  const [portalName, setPortalName] = useState("");
  const [mode, setMode] = useState("create");
  const [portalId, setPortalId] = useState([]);
  const [form] = Form.useForm();
  const onCancel = () => {
    setVisible(false);
  };
  const onEdit = (id) => {
    setMode("edit");
    setPortalId(id);
    setVisible(true);
    axiosInstance.get(`organization/${orgid}/portal/${id}`).then((response) => {
      console.log(response);
      setPortalName(response.data.data.attributes.name);
      let buff = new Buffer(response.data.data.attributes.definition, "base64");
      form.setFieldsValue({ definition: buff.toString("ascii") });
    });
  };

  const onNew = () => {
    form.resetFields();
    setVisible(true);
    setPortalName("");
    setMode("create");
  };

  const onDelete = (id) => {
    console.log("deleted " + id);
    axiosInstance
      .delete(`organization/${orgid}/portal/${id}`)
      .then((response) => {
        console.log(response);
        loadPortals();
      });
  };

  const onCreate = (values) => {
    const body = {
      data: {
        type: "portal",
        attributes: {
          name: values.name,
          definition: Buffer.from(values.definition).toString("base64"),
        },
      },
    };
    console.log(body);

    axiosInstance
      .post(`organization/${orgid}/portal`, body, {
        headers: {
          "Content-Type": "application/vnd.api+json",
        },
      })
      .then((response) => {
        console.log(response);
        loadPortals();
        setVisible(false);
        form.resetFields();
      });
  };

  const onUpdate = (values) => {
    const body = {
      data: {
        type: "portal",
        id: portalId,
        attributes: {
          definition: Buffer.from(values.definition).toString("base64"),
        },
      },
    };

    axiosInstance
      .patch(`organization/${orgid}/portal/${portalId}`, body, {
        headers: {
          "Content-Type": "application/vnd.api+json",
        },
      })
      .then((response) => {
        console.log(response);
        loadPortals();
        setVisible(false);
        form.resetFields();
      });
  };

  const loadPortals = () => {
    axiosInstance.get(`organization/${orgid}/portal`).then((response) => {
      console.log(response);
      setPortals(response.data);
      setLoading(false);
    });
  };
  useEffect(() => {
    setLoading(true);
    loadPortals();
  }, [orgid]);

  return (
    <div className="setting">
      <h1>Portals</h1>
      <div className="App-text">
        Portals let you define cloud agnostic service catalogs for your
        infrastructure using Terraform modules.
      </div>
      <Button type="primary" onClick={onNew} htmlType="button">
        Create portal
      </Button>
      <br></br>

      <h3 style={{ marginTop: "30px" }}>Portals</h3>
      {loading || !portals.data ? (
        <p>Data loading...</p>
      ) : (
        <List
          itemLayout="horizontal"
          dataSource={portals.data}
          renderItem={(item) => (
            <List.Item
              actions={[
                <Button
                  onClick={() => {
                    onEdit(item.id);
                  }}
                  icon={<EditOutlined />}
                  type="link"
                >
                  Edit
                </Button>,
                <Popconfirm
                  onConfirm={() => {
                    onDelete(item.id);
                  }}
                  style={{ width: "20px" }}
                  title={
                    <p>
                      This will permanently delete this portal. Are you sure?
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
                avatar={
                  <Avatar
                    style={{ backgroundColor: "#1890ff" }}
                    icon={<LayoutOutlined />}
                  ></Avatar>
                }
                title={item.attributes.name}
                description={
                  <a
                    href={
                      location.protocol +
                      "//" +
                      location.host +
                      "/organizations/" +
                      orgid +
                      "/portal/" +
                      item.id
                    }
                  >
                    {location.protocol +
                      "//" +
                      location.host +
                      "/organizations/" +
                      orgid +
                      "/portal/" +
                      item.id}
                  </a>
                }
              />
            </List.Item>
          )}
        />
      )}

      <Modal
        width="600px"
        visible={visible}
        title={
          mode === "edit" ? "Edit portal " + portalName : "Create new portal"
        }
        okText="Save portal"
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
          <Form name="portal" form={form} layout="vertical">
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
            <Form.Item
              name="definition"
              rules={[{ required: true }]}
              tooltip={{
                title: "Must be a valid Yaml definition",
                icon: <InfoCircleOutlined />,
              }}
              label="Definition"
            >
              <TextArea rows={8} />
            </Form.Item>
          </Form>
        </Space>
      </Modal>
    </div>
  );
};
