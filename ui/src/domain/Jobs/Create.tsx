import { React, useState, useEffect } from "react";
import {Form, Button, Select, Modal, Space, message, Input} from "antd";
import {
  ORGANIZATION_ARCHIVE,
  WORKSPACE_ARCHIVE,
} from "../../config/actionTypes";
import axiosInstance from "../../config/axiosConfig";
import {
  InfoCircleTwoTone,
  PlusOutlined,
  DeleteOutlined,
} from "@ant-design/icons";

const validateMessages = { required: "${label} is required!" };

export const CreateJob = ({ changeJob }) => {
  const workspaceId = sessionStorage.getItem(WORKSPACE_ARCHIVE);
  const organizationId = sessionStorage.getItem(ORGANIZATION_ARCHIVE);
  const [visible, setVisible] = useState(false);
  const [form] = Form.useForm();
  const [templates, setTemplates] = useState([]);
  const [branchName, setBranchName] = useState([]);
  const [loading, setLoading] = useState(false);
  const onCancel = () => {
    setVisible(false);
  };

  useEffect(() => {
    setLoading(true);
    loadTemplates();
    loadBranch();
  }, [organizationId]);

  const loadBranch = () => {
    axiosInstance
        .get(`organization/${organizationId}/workspace/${workspaceId}`)
        .then((response) => {
            console.log(response.data);
            console.log(response.data.data.attributes.branch);
          var branchName = response.data.data.attributes.branch;
          console.log("Default branch:" + branchName);
          setBranchName(branchName);
        });
  };

    const loadTemplates = () => {
        axiosInstance
            .get(`organization/${organizationId}/template`)
            .then((response) => {
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

  const onCreate = (values) => {
    const body = {
      data: {
        type: "job",
        attributes: {
          templateReference: values.templateId,
          overrideBranch: values.branchName,
          via: "UI",
        },
        relationships: {
          workspace: {
            data: {
              type: "workspace",
              id: workspaceId,
            },
          },
        },
      },
    };
    console.log(body);

    axiosInstance
        .post(`organization/${organizationId}/job`, body, {
          headers: {
            "Content-Type": "application/vnd.api+json",
          },
        })
        .then((response) => {
          console.log(response);
          setVisible(false);
          changeJob(response.data.data.id);
        }).catch((error) => {
      message.error('Not able to create job: ' + error.response.data.errors[0].detail)
      setVisible(false);
      console.log(error)
    });
  };

  return (
      <div>
        <Button
            type="primary"
            htmlType="button"
            onClick={() => {
              setVisible(true);
            }}
            icon={<PlusOutlined />}
        >
          New job
        </Button>

        <Modal
            open={visible}
            title="Start a new job"
            okText="Start"
            cancelText="Cancel"
            onCancel={onCancel}
            onOk={() => {
              form
                  .validateFields()
                  .then((values) => {
                    form.resetFields();
                    onCreate(values);
                  })
                  .catch((info) => {
                    console.log("Validate Failed:", info);
                  });
            }}
        >
          <Space direction="vertical">
            <div className="popup-text">
              <InfoCircleTwoTone style={{ fontSize: "16px" }} /> You will be
              redirected to the run details page to see this job executed.
            </div>
            <Form
                form={form}
                layout="vertical"
                name="create-org"
                validateMessages={validateMessages}
            >
              <Form.Item
                  name="templateId"
                  label="Choose job type"
                  rules={[{ required: true }]}
              >
                {loading || !templates ? (
                    <p>Data loading...</p>
                ) : (
                    <Select>
                      {templates.map((item) => (
                          <Select.Option key={item.id} value={item.id}>
                      <span
                          style={
                            item.attributes.name.includes("Destroy")
                                ? { color: "red" }
                                : {}
                          }
                      >
                        {item.attributes.name.includes("Destroy") && (
                            <DeleteOutlined style={{ marginRight: 8 }} />
                        )}
                        {item.attributes.name}
                      </span>
                          </Select.Option>
                      ))}
                    </Select>
                )}
              </Form.Item>
                <Form.Item
                    name="branchName"
                    label="Branch Name"
                    tooltip="Select the branch to use for this job. When using the CLI driven workflow do not modify the branch name."
                    initialValue={branchName}
                >
                    <Input />
                </Form.Item>
            </Form>
          </Space>
        </Modal>
      </div>
  );
};