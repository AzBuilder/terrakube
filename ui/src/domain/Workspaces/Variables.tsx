import { React, useState } from "react";
import {
    Form,
    Input,
    Button,
    Switch,
    Table,
    Modal,
    Space,
    Tag,
    Popconfirm, Tooltip,
} from "antd";
import {
  ORGANIZATION_ARCHIVE,
  WORKSPACE_ARCHIVE,
} from "../../config/actionTypes";
import axiosInstance from "../../config/axiosConfig";
import { InfoCircleOutlined } from "@ant-design/icons";
import { DeleteOutlined, EditOutlined } from "@ant-design/icons";

const VARIABLES_COLUMS = (organizationId, workspaceId, onEdit, manageWorkspace) => [
  {
    title: "Key",
    dataIndex: "key",
    width: "40%",
    key: "key",
    render: (_, record) => {
      return (
        <div>
          {record.key} &nbsp;&nbsp;&nbsp;&nbsp;{" "}
          {record.hcl && <Tag>HCL</Tag>}{" "}
          {record.sensitive && <Tag>Sensitive</Tag>}
        </div>
      );
    },
  },
  {
    title: "Value",
    dataIndex: "value",
    key: "value",
    width: "40%",
    render: (_, record) => {
      return record.sensitive ? (
        <i>Sensitive - write only</i>
      ) : (
          <Tooltip title={record.description} placement="topLeft" overlayStyle={{width: 400, wordBreak: "break-word"}}
                   overlayClassName="tooltip" trigger={["hover"]}>
              <div>{record.value}</div>
          </Tooltip>
      );
    },
  },
  {
    title: "Actions",
    key: "action",
    render: (_, record) => {
      return (
        <div>
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => onEdit(record)}
            disabled={!manageWorkspace}
          >
            Edit
          </Button>
          <Popconfirm
            onConfirm={() => {
              deleteVariable(record.id, organizationId, workspaceId);
            }}
            title={
              <p>
                This will permanently delete this variable <br />
                and it will no longer be used in future runs. <br />
                Are you sure?
              </p>
            }
            okText="Yes"
            cancelText="No"
          >
            {" "}
            <Button danger type="link" icon={<DeleteOutlined />} disabled={!manageWorkspace}>
              Delete
            </Button>
          </Popconfirm>
        </div>
      );
    },
  },
];

const COLLECTION_VARIABLES_COLUMNS = () => [
  {
    title: "Key",
    dataIndex: "key",
    width: "25%",
    key: "key",
    render: (_, record) => {
      return (
          <div>
            {record.key} &nbsp;&nbsp;&nbsp;&nbsp;{" "}
            {record.hcl && <Tag>HCL</Tag>}{" "}
            {record.sensitive && <Tag>Sensitive</Tag>}
          </div>
      );
    },
  },
  {
    title: "Value",
    dataIndex: "value",
    key: "value",
    width: "25%",
    render: (_, record) => {
      return record.sensitive ? (
          <i>Sensitive - write only</i>
      ) : (
          <Tooltip title={record.description} placement="topLeft" overlayStyle={{width: 400, wordBreak: "break-word"}}
                   overlayClassName="tooltip" trigger={["hover"]}>
              <div>{record.value}</div>
          </Tooltip>
      );
    },
  },
  {
    title: "Priority",
    dataIndex: "priority",
    width: "25%",
    key: "priority",
    render: (_, record) => {
      return (
          <div>
            {record.priority} &nbsp;&nbsp;&nbsp;&nbsp;{" "}
          </div>
      );
    },
  },
  {
    title: "Collection",
    dataIndex: "collectionName",
    width: "25%",
    key: "collectionName",
    render: (_, record) => {
      return (
          <div>
            {record.collectionName} &nbsp;&nbsp;&nbsp;&nbsp;{" "}
          </div>
      );
    },
  },
];

const GLOBAL_VARIABLES_COLUMNS = () => [
    {
        title: "Key",
        dataIndex: "key",
        width: "25%",
        key: "key",
        render: (_, record) => {
            return (
                <div>
                    {record.key} &nbsp;&nbsp;&nbsp;&nbsp;{" "}
                    {record.hcl && <Tag>HCL</Tag>}{" "}
                    {record.sensitive && <Tag>Sensitive</Tag>}
                </div>
            );
        },
    },
    {
        title: "Value",
        dataIndex: "value",
        key: "value",
        width: "25%",
        render: (_, record) => {
            return record.sensitive ? (
                <i>Sensitive - write only</i>
            ) : (
                <Tooltip title={record.description} placement="topLeft"
                         overlayStyle={{width: 400, wordBreak: "break-word"}} overlayClassName="tooltip"
                         trigger={["hover"]}>
                    <div>{record.value}</div>
                </Tooltip>
            );
        },
    }
];

const validateMessages = {
  required: "${label} is required!",
};

export const Variables = ({ vars, env, manageWorkspace, collectionVars, collectionEnvVars, globalVariables, globalEnvVariables }) => {
  const workspaceId = sessionStorage.getItem(WORKSPACE_ARCHIVE);
  const organizationId = sessionStorage.getItem(ORGANIZATION_ARCHIVE);
  const [form] = Form.useForm();
  const [visible, setVisible] = useState(false);
  const [variableName, setVariableName] = useState("");
  const [category, setCategory] = useState("TERRAFORM");
  const [mode, setMode] = useState("create");
  const [variableId, setVariableId] = useState("");
  const onCancel = () => {
    setVisible(false);
  };
  const onEdit = (variable) => {
    setMode("edit");
    setVariableId(variable.id);
    setVariableName(variable.key);
    form.setFieldsValue({
      key: variable.key,
      value: variable.value,
      sensitive: variable.sensitive,
      hcl: variable.hcl,
      description: variable.description,
    });
    setVisible(true);
    setCategory(variable.category);
  };

  const onCreate = (values) => {
    const body = {
      data: {
        type: "variable",
        attributes: {
          key: values.key,
          value: values.value,
          sensitive: values.sensitive,
          description: values.description,
          hcl: values.hcl,
          category: category,
        },
      },
    };
    console.log(body);

    axiosInstance
      .post(
        `organization/${organizationId}/workspace/${workspaceId}/variable`,
        body,
        {
          headers: {
            "Content-Type": "application/vnd.api+json",
          },
        }
      )
      .then((response) => {
        console.log(response);
        setVisible(false);
        form.resetFields();
      });
  };

  const onUpdate = (values) => {
    const body = {
      data: {
        type: "variable",
        id: variableId,
        attributes: {
          key: values.key,
          value: values.value,
          sensitive: values.sensitive,
          description: values.description,
          hcl: values.hcl,
          category: category,
        },
      },
    };
    console.log(body);

    axiosInstance
      .patch(
        `organization/${organizationId}/workspace/${workspaceId}/variable/${variableId}`,
        body,
        {
          headers: {
            "Content-Type": "application/vnd.api+json",
          },
        }
      )
      .then((response) => {
        console.log(response);
        setVisible(false);
        form.resetFields();
      });
  };

  return (
      <div>
          <h1>Variables</h1>
          <div className="App-text">
              <p>
                  These variables are used for all plans and applies in this
                  workspace.Workspaces using Terraform 0.10.0 or later can also load
                  default values from any *.auto.tfvars files in the configuration.
              </p>
              <p>
                  Sensitive variables are hidden from view in the UI and API, and can't
                  be edited. (To change a sensitive variable, delete and replace it.)
                  Sensitive variables can still appear in Terraform logs if your
                  configuration is designed to output them.
              </p>{" "}
          </div>
          <h2>Terraform Variables</h2>
          <div className="App-text">
              These Terraform variables are set using a terraform.tfvars file. To use
              interpolation or set a non-string value for a variable, click its HCL
              checkbox.
          </div>
          <Table
              dataSource={vars}
              columns={VARIABLES_COLUMS(organizationId, workspaceId, onEdit, manageWorkspace)}
              rowKey="key"
          />
          <Button
              type="primary"
              htmlType="button"
              onClick={() => {
                  setCategory("TERRAFORM");
                  setMode("create");
                  form.resetFields();
                  setVisible(true);
              }}
              disabled={!manageWorkspace}
          >
              Add variable
          </Button>
          <div className="envVariables">
              <h2>Environment Variables</h2>
              <div className="App-text">
                  These variables are set in Terraform's shell environment using export.
              </div>
              <Table
                  dataSource={env}
                  columns={VARIABLES_COLUMS(organizationId, workspaceId, onEdit, manageWorkspace)}
                  rowKey="key"
              />
              <Button
                  type="primary"
                  htmlType="button"
                  onClick={() => {
                      setCategory("ENV");
                      setMode("create");
                      form.resetFields();
                      setVisible(true);
                  }}
                  disabled={!manageWorkspace}
              >
                  Add variable
              </Button>
          </div>
          <div className="envVariables">
              <h2>Collection Values</h2>
              <div className="App-text">
                  <p>
                      The following values are taken from the collection used by this workspaces, this values are
                      injected
                      inside the Terrakube remote jobs.
                  </p>
              </div>
          </div>

          <div className="envVariables">
              <h2>Terraform Variables</h2>
              <Table
                  dataSource={collectionVars}
                  columns={COLLECTION_VARIABLES_COLUMNS()}
                  rowKey="key"
              />
          </div>

          <div className="envVariables">
              <h2>Environment Variables</h2>
              <Table
                  dataSource={collectionEnvVars}
                  columns={COLLECTION_VARIABLES_COLUMNS()}
                  rowKey="key"
              />
          </div>

          <div className="envVariables">
              <h2>Global Variables Values</h2>
              <div className="App-text">
                  <p>
                      The following values are taken from the organization global variables, this values are
                      injected inside the Terrakube remote jobs.
                  </p>
              </div>
          </div>

          <div className="envVariables">
              <h2>Terraform Global Variables</h2>
              <Table
                  dataSource={globalVariables}
                  columns={GLOBAL_VARIABLES_COLUMNS()}
                  rowKey="key"
              />
          </div>

          <div className="envVariables">
              <h2>Environment Global Variables</h2>
              <Table
                  dataSource={globalEnvVariables}
                  columns={GLOBAL_VARIABLES_COLUMNS()}
                  rowKey="key"
              />
          </div>

          <Modal
              width="600px"
              open={visible}
              title={
                  mode === "edit"
                      ? "Edit variable " + variableName
                      : "Create new variable"
              }
              okText="Save variable"
              cancelText="Cancel"
              onCancel={onCancel}
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
              <Space style={{width: "100%"}} direction="vertical">
                  <Form
                      name="create-org"
                      form={form}
                      layout="vertical"
                      validateMessages={validateMessages}
                  >
                      <Form.Item name="key" label="Key" rules={[{required: true}]}>
                          <Input/>
                      </Form.Item>
                      <Form.Item name="value" label="Value" rules={[{required: true}]}>
                          <Input/>
                      </Form.Item>
                      <Form.Item
                          name="hcl"
                          valuePropName="checked"
                          label="HCL"
                          tooltip={{
                              title:
                                  "Parse this field as HashiCorp Configuration Language (HCL). This allows you to interpolate values at runtime.",
                              icon: <InfoCircleOutlined/>,
                          }}
                      >
                          <Switch/>
                      </Form.Item>
                      <Form.Item
                          name="sensitive"
                          valuePropName="checked"
                          label="Sensitive"
                          tooltip={{
                              title:
                                  "Sensitive variables are never shown in the UI or API. They may appear in Terraform logs if your configuration is designed to output them.",
                              icon: <InfoCircleOutlined/>,
                          }}
                      >
                          <Switch/>
                      </Form.Item>
                      <Form.Item name="description" label="Description">
                          <Input.TextArea width="800px"/>
                      </Form.Item>
                  </Form>
              </Space>
          </Modal>
      </div>
  );
};

const deleteVariable = (variableId, organizationId, workspaceId) => {
    console.log(variableId);

    axiosInstance
        .delete(
            `organization/${organizationId}/workspace/${workspaceId}/variable/${variableId}`,
            {
                headers: {
                    "Content-Type": "application/vnd.api+json",
                },
            }
        )
        .then((response) => {
            console.log(response);
        });
};
