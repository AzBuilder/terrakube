import { React, useState, useEffect } from "react";
import {
  Form,
  Input,
  Button,
  Select,
  Table,
  Modal,
  Tag,
  Space,
  Popconfirm,
} from "antd";
import { Cron } from "react-js-cron";
import {
  ORGANIZATION_ARCHIVE,
  WORKSPACE_ARCHIVE,
} from "../../config/actionTypes";
import axiosInstance from "../../config/axiosConfig";
import {
  DeleteOutlined,
  EditOutlined,
  ClockCircleOutlined,
  InfoCircleOutlined,
} from "@ant-design/icons";
import cronstrue from "cronstrue";
var C2Q = require("cron-to-quartz");

const VARIABLES_COLUMS = (organizationId, workspaceId, onEdit) => [
  {
    title: "Id",
    dataIndex: "id",
    width: "30%",
    key: "id",
    render: (_, record) => {
      return record.id;
    },
  },
  {
    title: "Job Type",
    dataIndex: "jobType",
    key: "jobType",
    width: "10%",
    render: (_, record) => {
      return record.name;
    },
  },
  {
    title: "Schedule",
    dataIndex: "cron",
    key: "cron",
    width: "30%",
    render: (_, record) => {
      return (
        <span>
          <Tag color="default">cron: {record.cron} </Tag>
          <Tag icon={<InfoCircleOutlined />} color="default">
            {cronstrue.toString(record.cron, {
              dayOfWeekStartIndexZero: false,
            })}
          </Tag>
        </span>
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
          >
            Edit
          </Button>
          <Popconfirm
            onConfirm={() => {
              deleteSchedule(record.id, organizationId, workspaceId);
            }}
            title={
              <p>
                This will permanently delete this schedule <br />
                Are you sure?
              </p>
            }
            okText="Yes"
            cancelText="No"
          >
            {" "}
            <Button danger type="link" icon={<DeleteOutlined />}>
              Delete
            </Button>
          </Popconfirm>
        </div>
      );
    },
  },
];

const validateMessages = {
  required: "${label} is required!",
  pattern: "${label} is not valid cron expression!",
};

export const Schedules = ({ schedules }) => {
  const workspaceId = localStorage.getItem(WORKSPACE_ARCHIVE);
  const organizationId = localStorage.getItem(ORGANIZATION_ARCHIVE);
  const [form] = Form.useForm();
  const [visible, setVisible] = useState(false);
  const [loading, setLoading] = useState(false);
  const [mode, setMode] = useState("create");
  const [scheduleId, setScheduleId] = useState("");
  const [templates, setTemplates] = useState([]);
  const defaultValue = "* * * * *";
  const [value, setValue] = useState(defaultValue);

  useEffect(() => {
    setLoading(true);
    loadTemplates();
  }, [organizationId]);

  const loadTemplates = () => {
    axiosInstance
      .get(`organization/${organizationId}/template`)
      .then((response) => {
        console.log(response);
        var templatesList = response.data.data.filter(function (obj) {
          //exclude CLI based templates
          return (
            obj.attributes.name !== "Terraform-Plan/Apply-Cli" &&
            obj.attributes.name !== "Terraform-Plan/Destroy-Cli"
          );
        });
        setTemplates(templatesList);
        setLoading(false);
      });
  };

  const onCancel = () => {
    setVisible(false);
  };
  const onEdit = (schedule) => {
    setMode("edit");
    setScheduleId(schedule.id);
    form.setFieldsValue({
      templateId: schedule.templateReference,
      cron: schedule.cron,
    });
    setVisible(true);
  };

  const onCreate = (values) => {
    const body = {
      data: {
        type: "schedule",
        attributes: {
          templateReference: values.templateId,
          cron: C2Q.getQuartz(value)[0]?.join(" "),
        },
      },
    };
    console.log(body);

    axiosInstance
      .post(
        `organization/${organizationId}/workspace/${workspaceId}/schedule`,
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
        type: "schedule",
        id: scheduleId,
        attributes: {
          templateReference: values.templateId,
          cron: C2Q.getQuartz(value)[0]?.join(" "),
        },
      },
    };
    console.log(body);

    axiosInstance
      .patch(
        `organization/${organizationId}/workspace/${workspaceId}/schedule/${scheduleId}`,
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
      <h2>Schedules</h2>
      <div className="App-text">
        Schedules allows you to automatically trigger a Job in your workspace on
        a scheduled basis.
      </div>
      <Table
        dataSource={schedules}
        columns={VARIABLES_COLUMS(organizationId, workspaceId, onEdit)}
        rowKey="key"
      />
      <Button
        type="primary"
        icon={<ClockCircleOutlined />}
        htmlType="button"
        onClick={() => {
          setMode("create");
          form.resetFields();
          setVisible(true);
        }}
      >
        Add schedule
      </Button>

      <Modal
        width="600px"
        visible={visible}
        title={mode === "edit" ? "Edit schedule" : "Create new schedule"}
        okText="Save schedule"
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
        <Space style={{ width: "100%" }} direction="vertical">
          <Form
            name="create-org"
            form={form}
            layout="vertical"
            validateMessages={validateMessages}
          >
            <Form.Item
              name="templateId"
              label="Job Type"
              rules={[{ required: true }]}
              tooltip={{
                title: "Job to trigger in a scheduled basis",
                icon: <InfoCircleOutlined />,
              }}
            >
              {loading || !templates ? (
                <p>Data loading...</p>
              ) : (
                <Select>
                  {templates.map((item) => (
                    <Select.Option value={item.id}>
                      {item.attributes.name}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </Form.Item>
          </Form>
          <span>
            {" "}
            <span style={{ color: "#ff4d4f" }}>*</span> Cron
          </span>
          <Input name="cron" value={value} />
          <Cron value={value} setValue={setValue} />
        </Space>
      </Modal>
    </div>
  );
};

const deleteSchedule = (scheduleId, organizationId, workspaceId) => {
  console.log(scheduleId);

  axiosInstance
    .delete(
      `organization/${organizationId}/workspace/${workspaceId}/schedule/${scheduleId}`,
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
