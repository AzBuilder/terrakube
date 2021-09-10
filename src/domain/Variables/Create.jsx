import { React } from 'react';
import { Form, Input, Button, Select } from "antd";
import { ORGANIZATION_ARCHIVE, WORKSPACE_ARCHIVE } from '../../config/actionTypes';
import axiosInstance from "../../config/axiosConfig";

const layout = {
  labelCol: { span: 8 },
  wrapperCol: { span: 16 }
}

const validateMessages = {
  required: '${label} is required!'
}

export const CreateVariable = () => {
  const workspaceId = localStorage.getItem(WORKSPACE_ARCHIVE);
  const organizationId = localStorage.getItem(ORGANIZATION_ARCHIVE);

  const onFinish = (values) => {
    const body = {
      data: {
        type: values.type,
        attributes: { 
          key: values.key,
          value: values.value
        }
      }
    }
    console.log(body);

    axiosInstance.post(`organization/${organizationId}/workspace/${workspaceId}/${values.type}`, body, {
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
      <h2>Add new variable</h2>
      <Form {...layout} name="create-org" onFinish={onFinish} validateMessages={validateMessages}>
        <Form.Item name="key" label="Key" rules={[{required: true}]}>
          <Input />
        </Form.Item>
        <Form.Item name="value" label="Value" rules={[{required: true}]}>
          <Input />
        </Form.Item>
        <Form.Item name="type" label="Type" rules={[{required: true}]}>
          <Select>
            <Select.Option value="variable">Terraform</Select.Option>
            <Select.Option value="environment">Environment</Select.Option>
            <Select.Option value="secret">Secret</Select.Option>
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