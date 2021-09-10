import { React } from 'react';
import { Form, Button, Select } from "antd";
import { ORGANIZATION_ARCHIVE, WORKSPACE_ARCHIVE } from '../../config/actionTypes';
import axiosInstance from "../../config/axiosConfig";

const layout = {
  labelCol: { span: 14 },
  wrapperCol: { span: 100 }
}

const validateMessages = {
  required: '${label} is required!'
}

export const CreateJob = () => {
  const workspaceId = localStorage.getItem(WORKSPACE_ARCHIVE);
  const organizationId = localStorage.getItem(ORGANIZATION_ARCHIVE);

  const onFinish = (values) => {
    const body = {
      data: {
        type: "job",
        attributes: { 
          command: values.action
        },
        relationships: {
          workspace: {
            data: {
              type: "workspace",
              id: workspaceId
            }
          }
        }
      }
    }
    console.log(body);

    axiosInstance.post(`organization/${organizationId}/job`, body, {
      headers: {
        'Content-Type': 'application/vnd.api+json'
      }
    })
      .then(response => {
        console.log(response);
      })
  };

  return (
    <div className="createForm">
      <h2>Trigger new job</h2>
      <Form {...layout} name="create-org" onFinish={onFinish} validateMessages={validateMessages}>
        <Form.Item name="action" label="Action" rules={[{required: true}]}>
          <Select>
            <Select.Option value="plan">Plan</Select.Option>
            <Select.Option value="apply">Apply</Select.Option>
            <Select.Option value="destroy">Destroy</Select.Option>
          </Select>
        </Form.Item>
        <Form.Item wrapperCol={{ ...layout.wrapperCol, offset: 8 }}>
          <Button type="primary" htmlType="submit">
            Submit
          </Button>
        </Form.Item>
      </Form>
    </div>  
  )
}