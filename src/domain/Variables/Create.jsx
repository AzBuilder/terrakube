import { React, useState } from 'react';
import { Form, Input, Button, Switch,Select, Modal, Space } from "antd";
import { ORGANIZATION_ARCHIVE, WORKSPACE_ARCHIVE } from '../../config/actionTypes';
import axiosInstance from "../../config/axiosConfig";
import { useHistory } from "react-router-dom";
import {InfoCircleOutlined} from '@ant-design/icons';
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
  const history = useHistory();
  const onCreate = (values) => {
    const body = {
      data: {
        type: "variable",
        attributes: {
          key: values.key,
          value: values.value,
          sensitive: values.sensitive,
          description:values.description,
          hcl: values.hcl,
          category: varType
        }
      }
    }
    console.log(body);

    axiosInstance.post(`organization/${organizationId}/workspace/${workspaceId}/variable`, body, {
      headers: {
        'Content-Type': 'application/vnd.api+json'
      }
    })
      .then(response => {
        console.log(response);
        setVisible(false);
      });
  };

  return (
    <div>
      <Button type="primary" htmlType="button"
        onClick={() => {
          setVisible(true);
        }}>
        Add variable
      </Button>
      <Modal width="600px" visible={visible} title="Add new variable" okText="Save variable" cancelText="Cancel" onCancel={onCancel}
        onOk={() => {
          form.validateFields().then((values) => {
            form.resetFields();
            onCreate(values);
          }).catch((info) => {
            console.log('Validate Failed:', info);
          });
        }}>
        <Space style={{width:"100%"}} direction="vertical">
          <Form name="create-org" form={form} layout="vertical" validateMessages={validateMessages} initialValues={{ type: varType }}>
            <Form.Item name="key" label="Key" rules={[{ required: true }]}>
              <Input />
            </Form.Item>
            <Form.Item name="value" label="Value" rules={[{ required: true }]}>
              <Input />
            </Form.Item>
            <Form.Item name="hcl" label="HCL" tooltip={{ title: 'Parse this field as HashiCorp Configuration Language (HCL). This allows you to interpolate values at runtime.', icon: <InfoCircleOutlined /> }} >
              <Switch />
           </Form.Item>
           <Form.Item name="sensitive" label="Sensitive" tooltip={{ title: 'Sensitive variables are never shown in the UI or API. They may appear in Terraform logs if your configuration is designed to output them.', icon: <InfoCircleOutlined /> }} >
              <Switch />
           </Form.Item>
           <Form.Item name="description"  label="Description">
              <Input.TextArea width="800px" />
          </Form.Item>
          </Form>
        </Space>
      </Modal>
    </div>
  )
}