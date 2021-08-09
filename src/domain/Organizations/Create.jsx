import { React } from "react";
import { Form, Input, Button } from "antd";
import axiosInstance from "../../config/axiosConfig";

const layout = {
  labelCol: { span: 8 },
  wrapperCol: { span: 16 }
}

const validateMessages = {
  required: '${label} is required!'
}

export const CreateOrganization = () => {
  const onFinish = (values) => {
    const body = {
      data: {
        type: "organization",
        attributes: values
      }
    }
    console.log(body);

    axiosInstance.post("organization", body, {
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
      <h2>Create Organization</h2>
      <Form {...layout} name="create-org" onFinish={onFinish} validateMessages={validateMessages}>
        <Form.Item name="name" label="Name" rules={[{required: true}]}>
          <Input />
        </Form.Item>
        <Form.Item name="description" label="Description">
          <Input.TextArea />
        </Form.Item>
        <Form.Item wrapperCol={{ ...layout.wrapperCol, offset: 8 }}>
          <Button type="primary" htmlType="submit">
            Submit
          </Button>
        </Form.Item>
      </Form>
    </div>
  );
}