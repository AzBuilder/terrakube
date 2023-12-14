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

export const ServiceCatalogSettings = () => {
  const { orgid } = useParams();
  const [serviceCatalogs, setServiceCatalogs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [visible, setVisible] = useState(false);
  const [serviceCatalogName, setServiceCatalogName] = useState("");
  const [mode, setMode] = useState("create");
  const [serviceCatalogId, setServiceCatalogId] = useState([]);
  const [form] = Form.useForm();
  const onCancel = () => {
    setVisible(false);
  };
  const onEdit = (id) => {
    setMode("edit");
    setServiceCatalogId(id);
    setVisible(true);
    axiosInstance.get(`organization/${orgid}/servicecatalog/${id}`).then((response) => {
      console.log(response);
      setServiceCatalogName(response.data.data.attributes.name);
      let buff = new Buffer(response.data.data.attributes.definition, "base64");
      form.setFieldsValue({ definition: buff.toString("ascii") });
    });
  };

  const onNew = () => {
    form.resetFields();
    setVisible(true);
    setServiceCatalogName("");
    setMode("create");
  };

  const onDelete = (id) => {
    console.log("deleted " + id);
    axiosInstance
      .delete(`organization/${orgid}/servicecatalog/${id}`)
      .then((response) => {
        console.log(response);
        loadServiceCatalogs();
      });
  };

  const onCreate = (values) => {
    const body = {
      data: {
        type: "serviceCatalog",
        attributes: {
          name: values.name,
          definition: Buffer.from(values.definition).toString("base64"),
        },
      },
    };
    console.log(body);

    axiosInstance
      .post(`organization/${orgid}/servicecatalog`, body, {
        headers: {
          "Content-Type": "application/vnd.api+json",
        },
      })
      .then((response) => {
        console.log(response);
        loadServiceCatalogs();
        setVisible(false);
        form.resetFields();
      });
  };

  const onUpdate = (values) => {
    const body = {
      data: {
        type: "serviceCatalog",
        id: serviceCatalogId,
        attributes: {
          definition: Buffer.from(values.definition).toString("base64"),
        },
      },
    };

    axiosInstance
      .patch(`organization/${orgid}/servicecatalog/${serviceCatalogId}`, body, {
        headers: {
          "Content-Type": "application/vnd.api+json",
        },
      })
      .then((response) => {
        console.log(response);
        loadServiceCatalogs();
        setVisible(false);
        form.resetFields();
      });
  };

  const loadServiceCatalogs = () => {
    axiosInstance.get(`organization/${orgid}/servicecatalog`).then((response) => {
      console.log(response);
      setServiceCatalogs(response.data);
      setLoading(false);
    });
  };
  useEffect(() => {
    setLoading(true);
    loadServiceCatalogs();
  }, [orgid]);

  return (
    <div className="setting">
      <h1>Service Catalogs</h1>
      <div className="App-text">
        Service Catalogs allow you to define a catalog of cloud-agnostic services for your infrastructure using Terraform modules.
      </div>
      <Button type="primary" onClick={onNew} htmlType="button">
        Create Service Catalog
      </Button>
      <br></br>

      <h3 style={{ marginTop: "30px" }}>Service Catalogs</h3>
      {loading || !serviceCatalogs.data ? (
        <p>Data loading...</p>
      ) : (
        <List
          itemLayout="horizontal"
          dataSource={serviceCatalogs.data}
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
                      This will permanently delete this service catalog. Are you sure?
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
                      "/servicecatalog/" +
                      item.id
                    }
                  >
                    {location.protocol +
                      "//" +
                      location.host +
                      "/organizations/" +
                      orgid +
                      "/servicecatalog/" +
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
          mode === "edit" ? "Edit service catalog " + serviceCatalogName : "Create new servicecatalog"
        }
        okText="Save service catalog"
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
          <Form name="servicecatalog" form={form} layout="vertical">
            {mode === "create" ? (
              <Form.Item
                name="name"
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
