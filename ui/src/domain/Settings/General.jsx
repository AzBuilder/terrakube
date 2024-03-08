import { React, useState, useEffect } from "react";
import { Button, Form, Input, message, Spin, Popconfirm, Space,Radio } from "antd";
import axiosInstance from "../../config/axiosConfig";
import { useParams } from "react-router-dom";
import { DeleteOutlined } from "@ant-design/icons";
import "./Settings.css";

export const GeneralSettings = () => {
  const { orgid } = useParams();
  const [organization, setOrganization] = useState([]);
  const [loading, setLoading] = useState(false);
  const [waiting, setWaiting] = useState(false);
  useEffect(() => {
    setLoading(true);
    axiosInstance.get(`organization/${orgid}`).then((response) => {
      console.log(response);
      setOrganization(response.data);
      setLoading(false);
    });
  }, [orgid]);

  const onFinish = (values) => {
    setWaiting(true);
    const body = {
      data: {
        type: "organization",
        id: orgid,
        attributes: {
          name: values.name,
          description: values.description,
          executionMode: values.executionMode,
        },
      },
    };
    console.log(body);

    axiosInstance
      .patch(`organization/${orgid}`, body, {
        headers: {
          "Content-Type": "application/vnd.api+json",
        },
      })
      .then((response) => {
        console.log(response);
        if (response.status == "204") {
          message.success("Organization updated successfully");
        } else {
          message.error("Organization update failed");
        }
        setWaiting(false);
      });
  };

  const onDelete = (values) => {
    const body = {
      data: {
        type: "organization",
        id: orgid,
        attributes: {
          disabled: "true",
        },
      },
    };

    axiosInstance
      .patch(`organization/${orgid}`, body, {
        headers: {
          "Content-Type": "application/vnd.api+json",
        },
      })
      .then((response) => {
        console.log(response);
        if (response.status == "204") {
          console.log(response);
          message.success(
            "Organization deleted successfully, please logout and login to Terrakube"
          );
        } else {
          message.error("Organization deletion failed");
        }
      });
  };

  return (
    <div className="setting">
      <h1>General Settings</h1>
      {loading || !organization.data ? (
        <p>Data loading...</p>
      ) : (
        <Spin spinning={waiting}>
          <Form onFinish={onFinish} layout="vertical" name="form-settings">
            <Form.Item name="name" label="Name">
              <Input defaultValue={organization.data.attributes.name} />
            </Form.Item>
            <Form.Item name="description" label="Description">
              <Input.TextArea
                defaultValue={organization.data.attributes.description}
              />
            </Form.Item>
            <Form.Item name="executionMode" label="Default Exection Mode">
              <Radio.Group defaultValue={organization.data.attributes.executionMode}>
                <Space direction="vertical">
                  <Radio value="remote">
                    <b>Remote</b>
                    <p style={{color:"#656a76"}}>
                      Terrakube hosts your plans and applies, allowing you and your team to collaborate and review jobs in the app.
                    </p>
                  </Radio>
                  <Radio value="local">
                   <b>Local</b>
                   <p style={{color:"#656a76"}}>
                     Your planning and applying jobs are performed on your own machines. Terrakube is used just for storing and syncing the state.
                    </p>
                  </Radio>
                </Space>
              </Radio.Group>
            </Form.Item>
            <Form.Item>
              <Button type="primary" htmlType="submit">
                Update organization
              </Button>
            </Form.Item>
          </Form>
        </Spin>
      )}
      <h1>Delete this Organization</h1>
      <div className="App-text">
        Deleting the organization will permanently delete all workspaces
        associated with it. Please be certain that you understand this.
      </div>
      <Popconfirm
        onConfirm={() => {
          onDelete(orgid);
        }}
        style={{ width: "100%" }}
        title={
          <p>
            Organization will be permanently deleted and all workspaces will be
            marked as deleted <br />
            <br />
            Are you sure?
          </p>
        }
        okText="Yes"
        cancelText="No"
        placement="bottom"
      >
        <Button type="default" danger style={{ width: "100%" }}>
          <Space>
            <DeleteOutlined />
            Delete from Terrakube
          </Space>
        </Button>
      </Popconfirm>
    </div>
  );
};
