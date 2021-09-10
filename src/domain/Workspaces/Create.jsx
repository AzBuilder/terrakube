import { React } from 'react';
import { Form, Input, Button } from "antd";
import { ORGANIZATION_ARCHIVE } from '../../config/actionTypes';
import axiosInstance from "../../config/axiosConfig";

const layout = {
  labelCol: { span: 12 },
  wrapperCol: { span: 16 }
}

const validateMessages = {
  required: '${label} is required!'
}

export const CreateWorkspace = () => {
  const organizationId = localStorage.getItem(ORGANIZATION_ARCHIVE);

  const onFinish = (values) => {
    const body = {
      data: {
        type: "workspace",
        attributes: values
      }
    }
    console.log(body);

    axiosInstance.post(`organization/${organizationId}/workspace`, body, {
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
      <h2>Create new workspace</h2>
      <Form {...layout} name="create-workspace" onFinish={onFinish} validateMessages={validateMessages}>
        <Form.Item name="name" label="Name" rules={[{required: true}]}>
          <Input />
        </Form.Item>
        <Form.Item name="source" label="Source" rules={[{required: true}]}>
          <Input />
        </Form.Item>
        <Form.Item name="branch" label="Branch" rules={[{required: true}]}>
          <Input />
        </Form.Item>
        <Form.Item name="terraformVersion" label="Terraform Version" rules={[{required: true}]}>
          <Input />
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