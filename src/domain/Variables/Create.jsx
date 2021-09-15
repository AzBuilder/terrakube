import { React, useState } from 'react';
import { Form, Input, Button, Select, Modal, Space } from "antd";
import { ORGANIZATION_ARCHIVE, WORKSPACE_ARCHIVE } from '../../config/actionTypes';
import axiosInstance from "../../config/axiosConfig";

const layout = {
  labelCol: { span: 8 },
  wrapperCol: { span: 16 }
}

const validateMessages = {
  required: '${label} is required!'
}

export const CreateVariable = ({varType}) => {
  const workspaceId = localStorage.getItem(WORKSPACE_ARCHIVE);
  const organizationId = localStorage.getItem(ORGANIZATION_ARCHIVE);
  const [form] = Form.useForm();
  const [visible, setVisible] = useState(false);
  const onCancel = () => {
    setVisible(false);
  };
  const onCreate = (values) => {
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
        setVisible(false);
      })
  };

  return (
    <div>
      <Button type="primary" htmlType="button"
        onClick={() => {
          setVisible(true);
        }}>
        Add variable
      </Button>
      <Modal visible={visible} title="Add new variable" okText="Save variable" cancelText="Cancel" onCancel={onCancel}
        onOk={() => {
          form.validateFields().then((values) => {
            form.resetFields();
            onCreate(values);
          }).catch((info) => {
            console.log('Validate Failed:', info);
          });
        }}>
        <Space direction="vertical">
          <Form name="create-org" form={form} layout="vertical" validateMessages={validateMessages} initialValues={{ type: varType }}>
            <Form.Item name="key" label="Key" rules={[{ required: true }]}>
              <Input />
            </Form.Item>
            <Form.Item name="value" label="Value" rules={[{ required: true }]}>
              <Input />
            </Form.Item>
            <Form.Item name="type" hidden="true">
              <Select>
                <Select.Option value="variable">Terraform</Select.Option>
                <Select.Option value="environment">Environment</Select.Option>
                <Select.Option value="secret">Secret</Select.Option>
              </Select>
            </Form.Item>
          </Form>
        </Space>
      </Modal>
    </div>
  )
}