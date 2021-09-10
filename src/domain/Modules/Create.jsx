import { React } from 'react';
import { Form, Input, Button, Select } from "antd";
import { ORGANIZATION_ARCHIVE } from '../../config/actionTypes';
import axiosInstance from "../../config/axiosConfig";

const layout = {
  labelCol: { span: 14 },
  wrapperCol: { span: 100 }
}

const validateMessages = {
  required: '${label} is required!'
}

export const CreateModule = () => {
  const organizationId = localStorage.getItem(ORGANIZATION_ARCHIVE);

  const onFinish = (values) => {
    const body = {
      data: {
        type: "module",
        attributes: { 
          name: values.name,
          description: values.description,
          provider: values.provider,
          source: values.source,
          sourceSample: values.source
        }
      }
    }
    console.log(body);

    axiosInstance.post(`organization/${organizationId}/module`, body, {
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
      <Form {...layout} name="create-mod" onFinish={onFinish} validateMessages={validateMessages}>
        <Form.Item name="name" label="Name" rules={[{required: true}]}>
          <Input />
        </Form.Item>
        <Form.Item name="description" label="Description" rules={[{required: true}]}>
          <Input.TextArea />
        </Form.Item>
        <Form.Item name="source" label="Source" rules={[{required: true}]}>
          <Input />
        </Form.Item>
        <Form.Item name="provider" label="Provider" rules={[{required: true}]}>
          <Select>
            <Select.Option value="azurerm">Azure</Select.Option>
            <Select.Option value="google">Google</Select.Option>
            <Select.Option value="aws">AWS</Select.Option>
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