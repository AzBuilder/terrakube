import { React, useState, useEffect } from "react";
import {
  Button,
  List,
  Popconfirm,
  Form,
  Modal,
  Space,
  Input,
  Switch,
  Avatar,
  Divider,
} from "antd";
import "./Settings.css";
import axiosInstance from "../../config/axiosConfig";
import { useParams } from "react-router-dom";
import {
  InfoCircleOutlined,
  TagOutlined,
  EditOutlined,
  DeleteOutlined,
} from "@ant-design/icons";
export const TagsSettings = () => {
  const { orgid } = useParams();
  const [tags, setTags] = useState([]);
  const [loading, setLoading] = useState(false);
  const [visible, setVisible] = useState(false);
  const [tagName, setTagName] = useState(false);
  const [mode, setMode] = useState("create");
  const [tagId, setTagId] = useState([]);
  const [form] = Form.useForm();
  const onCancel = () => {
    setVisible(false);
  };
  const onEdit = (id) => {
    setMode("edit");
    setTagId(id);
    setVisible(true);
    axiosInstance.get(`organization/${orgid}/tag/${id}`).then((response) => {
      console.log(response);
      setTagName(response.data.data.attributes.name);
      form.setFieldsValue({
        name: response.data.data.attributes.name,
      });
    });
  };

  const onNew = () => {
    form.resetFields();
    setVisible(true);
    setTagName("");
    setMode("create");
  };

  const onDelete = (id) => {
    console.log("deleted " + id);
    axiosInstance.delete(`organization/${orgid}/tag/${id}`).then((response) => {
      console.log(response);
      loadTags();
    });
  };

  const onCreate = (values) => {
    const body = {
      data: {
        type: "tag",
        attributes: {
          name: values.name,
        },
      },
    };
    console.log(body);

    axiosInstance
      .post(`organization/${orgid}/tag`, body, {
        headers: {
          "Content-Type": "application/vnd.api+json",
        },
      })
      .then((response) => {
        console.log(response);
        loadTags();
        setVisible(false);
        form.resetFields();
      });
  };

  const onUpdate = (values) => {
    const body = {
      data: {
        type: "tag",
        id: tagId,
        attributes: {
          name: values.name,
        },
      },
    };
    console.log(body);

    axiosInstance
      .patch(`organization/${orgid}/tag/${tagId}`, body, {
        headers: {
          "Content-Type": "application/vnd.api+json",
        },
      })
      .then((response) => {
        console.log(response);
        loadTags();
        setVisible(false);
        form.resetFields();
      });
  };

  const loadTags = () => {
    axiosInstance.get(`organization/${orgid}/tag`).then((response) => {
      console.log(response);
      setTags(response.data);
      setLoading(false);
    });
  };
  useEffect(() => {
    setLoading(true);
    loadTags();
  }, [orgid]);

  return (
    <div className="setting">
      <h1>Tag Management</h1>
      <div className="App-text">
        Tags are used to help identify and group together workspaces..
      </div>
      <Button type="primary" onClick={onNew} htmlType="button">
        Create tag
      </Button>
      <br></br>

      <h3 style={{ marginTop: "30px" }}>Tags</h3>
      {loading || !tags.data ? (
        <p>Data loading...</p>
      ) : (
        <List
          itemLayout="horizontal"
          dataSource={tags.data}
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
                      Deleting this tag will also remove it <br />
                      from all the Workspaces that use it.
                      <br />
                      This action cannot be undone. <br />
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
                avatar={
                  <Avatar
                    style={{ backgroundColor: "#1890ff" }}
                    icon={<TagOutlined />}
                  ></Avatar>
                }
                title={item.attributes.name}
              />
            </List.Item>
          )}
        />
      )}

      <Modal
        width="600px"
        visible={visible}
        title={mode === "edit" ? "Edit tag " + tagName : "Create new tag"}
        okText="Save tag"
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
          <Form name="tag" form={form} layout="vertical">
            <Form.Item
              name="name"
              tooltip={{
                title: "Must be a valid tag name",
                icon: <InfoCircleOutlined />,
              }}
              label="Name"
              rules={[{ required: true }]}
            >
              <Input />
            </Form.Item>
          </Form>
        </Space>
      </Modal>
    </div>
  );
};
