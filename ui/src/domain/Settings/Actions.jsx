import { React, useState, useEffect, useRef } from "react";
import { Button, Table, Popconfirm, Form, Space, Input, Select, Switch, Tooltip } from "antd";
import { MinusCircleOutlined, PlusOutlined, EditOutlined, DeleteOutlined, InfoCircleOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import axiosInstance from "../../config/axiosConfig";
import Editor from "@monaco-editor/react";
import './Settings.css';
import { Buffer } from "buffer";

const validateMessages = {
  required: '${label} is required!',
  types: {
    version: '${label} is not a valid semver!'
  },
  pattern: {
    mismatch: '${label} is not a valid semver!'
  }
};

export const ActionSettings = () => {
  const [actions, setActions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [mode, setMode] = useState("create");
  const [actionId, setActionId] = useState(null);
  const [form] = Form.useForm();
  const editorRef = useRef(null);

  const ACTIONS_COLUMNS = (onEdit) => [
    {
      title: 'Name',
      dataIndex: 'name',
      key: 'name',
      render: (_, record) => <div>{record.attributes.name}</div>,
    },
    {
      title: 'Type',
      dataIndex: 'type',
      key: 'type',
      render: (_, record) => <div>{record.attributes.type}</div>,
    },
    {
      title: 'Category',
      dataIndex: 'category',
      key: 'category',
      render: (_, record) => <div>{record.attributes.category}</div>,
    },
    {
      title: 'Version',
      dataIndex: 'version',
      key: 'version',
      render: (_, record) => <div>{record.attributes.version}</div>,
    },
    {
      title: 'Active',
      dataIndex: 'active',
      key: 'active',
      render: (_, record) => <Switch checked={record.attributes.active} disabled />,
    },
    {
      title: 'Actions',
      key: 'action',
      render: (_, record) => (
        <div>
          <Button type="link" icon={<EditOutlined />} onClick={() => onEdit(record.id)}>Edit</Button>
          <Popconfirm title="Are you sure to delete this action?" onConfirm={() => onDelete(record.id)} okText="Yes" cancelText="No">
            <Button danger type="link" icon={<DeleteOutlined />}>Delete</Button>
          </Popconfirm>
          <Tooltip title="Open the documentation for this Action">
            <Button icon={<QuestionCircleOutlined />} target="_blank" rel="noreferrer" href={"https://docs.terrakube.io/user-guide/workspaces/actions/built-in-actions/" + record.id} type="link"></Button>
          </Tooltip>
        </div>
      ),
    }
  ];

  const onCancel = () => {
    setIsEditing(false);
    form.resetFields();
  };

  const onEdit = (id) => {
    setMode("edit");
    setActionId(id);
    setIsEditing(true);
    axiosInstance.get(`action/${id}`)
      .then(response => {
        const action = response.data.data;
        form.setFieldsValue({ id: action.id, ...action.attributes, displayCriteria: JSON.parse(action.attributes.displayCriteria) });
        const actionDecoded = Buffer.from(action.attributes.action, 'base64').toString('ascii');
        editorRef.current.setValue(actionDecoded);
      });
  };

  const onNew = () => {
    form.resetFields();
    setIsEditing(true);
    setMode("create");
  };

  const onDelete = (id) => {
    axiosInstance.delete(`action/${id}`)
      .then(response => {
        loadActions();
      });
  };

  const onCreate = (values) => {
    const actionEncoded = Buffer.from(editorRef.current.getValue()).toString('base64');
    const displayCriteria = JSON.stringify(values.displayCriteria);
    const body = {
      data: {
        type: "action",
        id: values.id,
        attributes: { ...values, action: actionEncoded, displayCriteria }
      }
    };
    axiosInstance.post(`action`, body, {
      headers: {
        'Content-Type': 'application/vnd.api+json'
      }
    })
      .then(response => {
        loadActions();
        setIsEditing(false);
        form.resetFields();
      });
  };

  const onUpdate = (values) => {
    const actionEncoded = Buffer.from(editorRef.current.getValue()).toString('base64');
    const displayCriteria = JSON.stringify(values.displayCriteria);
    const body = {
      data: {
        type: "action",
        id: actionId,
        attributes: { ...values, action: actionEncoded, displayCriteria }
      }
    };
    axiosInstance.patch(`action/${actionId}`, body, {
      headers: {
        'Content-Type': 'application/vnd.api+json'
      }
    })
      .then(response => {
        loadActions();
        setIsEditing(false);
        form.resetFields();
      });
  };

  const loadActions = () => {
    axiosInstance.get(`action`)
      .then(response => {
        setActions(response.data);
        setLoading(false);
      });
  };

  useEffect(() => {
    setLoading(true);
    loadActions();
  }, []);

  function handleEditorDidMount(editor, monaco) {
    editorRef.current = editor;
  }

  return (
    <div className="setting">
      <h1>Actions</h1>
      <div className="App-text">
        Actions are used to extend the Terrakube UI. For example, you can add a new button to restart a VM directly from Terrakube.
      </div>
      {!isEditing ? (
        <>
          <Button type="primary" icon={<PlusOutlined />} onClick={onNew}>Create Action</Button>
          <h3 style={{ marginTop: "30px" }}>Actions</h3>
          {loading || !actions.data ? (
            <p>Data loading...</p>
          ) : (
            <Table dataSource={actions.data} columns={ACTIONS_COLUMNS(onEdit)} rowKey='id' />
          )}
        </>
      ) : (
        <div>
          <h3>{mode === "edit" ? "Edit Action" : "Create New Action"}</h3>
          {mode === "edit" ? (
            <Tooltip title="Open the documentation for this Action">
              <Button icon={<QuestionCircleOutlined />} target="_blank" rel="noreferrer" href={"https://docs.terrakube.io/user-guide/workspaces/actions/built-in-actions/" + actionId} type="link">Action Documentation</Button>
            </Tooltip>
          ) : (
            <Tooltip title="See a quick start guide in how to create Actions">
              <Button icon={<QuestionCircleOutlined />} target="_blank" rel="noreferrer" href={"https://docs.terrakube.io/user-guide/workspaces/actions/developing-actions/quick-start"} type="link">Actions Documentation</Button>
            </Tooltip>
          )}
          <Form
            form={form}
            layout="vertical"
            onFinish={(values) => {
              if (mode === "create") onCreate(values);
              else onUpdate(values);
            }}
            validateMessages={validateMessages}
          >
            <Form.Item name="id" label="ID" rules={[{ required: true }]}>
              <Input disabled={mode !== "create"} />
            </Form.Item>
            <Form.Item name="name" label="Name" rules={[{ required: true }]}>
              <Input />
            </Form.Item>
            <Form.Item
              name="type"
              label="Type"
              tooltip={{
                title: "Defines the section where this action will appear. Check the docs to see the specific area where the action will be rendered.",
                icon: <InfoCircleOutlined />,
              }}
              rules={[{ required: true }]}
            >
              <Select placeholder="Please select a type">
                <Select.Option value="Workspace/Action">Workspace/Action</Select.Option>
                <Select.Option value="Workspace/ResourceDrawer/Action">Workspace/ResourceDrawer/Action</Select.Option>
                <Select.Option value="Workspace/ResourceDrawer/Tab">Workspace/ResourceDrawer/Tab</Select.Option>
              </Select>
            </Form.Item>
            <Form.Item
              name="label"
              label="Label"
              tooltip={{
                title: "For tabs, this will be displayed as the tab name. For action buttons, it should be displayed as the button name.",
                icon: <InfoCircleOutlined />,
              }}
              rules={[{ required: true }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="category"
              label="Category"
              tooltip={{
                title: "This helps to organize the actions based on their function. Example: General, Azure, Cost, Monitoring.",
                icon: <InfoCircleOutlined />,
              }}
              rules={[{ required: true }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="version"
              label="Version"
              tooltip={{
                title: "Must follow semantic versioning (e.g., 1.0.0).",
                icon: <InfoCircleOutlined />,
              }}
              rules={[
                { required: true },
                { pattern: new RegExp(/^([0-9]+)\.([0-9]+)\.([0-9]+)$/), message: 'Version must be in semver format (e.g., 1.0.0)' }
              ]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="description"
              label="Description"
              tooltip={{
                title: "A brief description of the action.",
                icon: <InfoCircleOutlined />,
              }}
            >
              <Input.TextArea />
            </Form.Item>
            <Form.List name="displayCriteria">
              {(fields, { add, remove }) => (
                <>
                  {fields.map(({ key, name, ...restField }) => (
                    <div key={key} style={{ marginBottom: 16 }}>
                      <Space style={{ display: 'flex', justifyContent: 'space-between', width: '100%' }} align="baseline">
                        <Form.Item
                          {...restField}
                          name={[name, 'filter']}
                          tooltip={{
                            title: "Defines if the action appears or not.",
                            icon: <InfoCircleOutlined />,
                          }}
                          style={{ width: 'calc(100% - 24px)' }}
                          rules={[{ required: true, message: 'Missing filter' }]}
                        >
                          <Input placeholder="Filter" />
                        </Form.Item>
                        <Tooltip title="Remove Display Criteria">
                          <MinusCircleOutlined onClick={() => remove(name)} />
                        </Tooltip>
                      </Space>
                      <Form.List name={[name, 'settings']}>
                        {(settingFields, { add: addSetting, remove: removeSetting }) => (
                          <>
                            {settingFields.map(({ key: settingKey, name: settingName, ...settingRestField }) => (
                              <Space key={settingKey} style={{ display: 'flex', marginBottom: 8, marginLeft: 24 }} align="baseline">
                                <Form.Item
                                  {...settingRestField}
                                  name={[settingName, 'key']}
                                  rules={[{ required: true, message: 'Missing key' }]}
                                >
                                  <Input placeholder="Key" />
                                </Form.Item>
                                <Form.Item
                                  {...settingRestField}
                                  name={[settingName, 'value']}
                                  rules={[{ required: true, message: 'Missing value' }]}
                                >
                                  <Input placeholder="Value" />
                                </Form.Item>
                                <Tooltip title="Remove Setting">
                                  <MinusCircleOutlined onClick={() => removeSetting(settingName)} />
                                </Tooltip>
                              </Space>
                            ))}
                            <Form.Item style={{ marginLeft: 24 }}>
                              <Button type="dashed" onClick={() => addSetting()} block icon={<PlusOutlined />}>
                                Add Setting
                              </Button>
                            </Form.Item>
                          </>
                        )}
                      </Form.List>
                    </div>
                  ))}
                  <Form.Item>
                    <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined />}>
                      Add Display Criteria
                    </Button>
                  </Form.Item>
                </>
              )}
            </Form.List>
            <Form.Item
              label="Action"
              tooltip={{
                title: "A JavaScript function equivalent to a React component. Receives the context with some data related to the context. This varies by type, please check the docs.",
                icon: <InfoCircleOutlined />,
              }}
            >
              <div className="editor">
                <Editor height="40vh" onMount={handleEditorDidMount} defaultLanguage="javascript" />
              </div>
            </Form.Item>
            <Form.Item name="active" valuePropName="checked" label="Active">
              <Switch />
            </Form.Item>
            <Form.Item>
              <Button type="primary" htmlType="submit">Save</Button>
              <Button type="default" onClick={onCancel} style={{ marginLeft: "10px" }}>Cancel</Button>
            </Form.Item>
          </Form>
        </div>
      )}
    </div>
  );
};