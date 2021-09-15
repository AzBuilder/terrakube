import { React, useState } from 'react';
import { Form, Button, Select, Modal,Space } from "antd";
import { ORGANIZATION_ARCHIVE, WORKSPACE_ARCHIVE } from '../../config/actionTypes';
import axiosInstance from "../../config/axiosConfig";
import {InfoCircleTwoTone} from '@ant-design/icons';

const validateMessages = {required: '${label} is required!'}

export const CreateJob = () => {
  const workspaceId = localStorage.getItem(WORKSPACE_ARCHIVE);
  const organizationId = localStorage.getItem(ORGANIZATION_ARCHIVE);
  const [visible, setVisible] = useState(false);
  const [form] = Form.useForm();
  const onCancel = () => {
    setVisible(false);
  };

  const onCreate = (values) => {
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
        setVisible(false);
      });
    
  };

  return (

    <div>
      <Button type="primary" htmlType="button"
        onClick={() => {
          setVisible(true);
        }}>
        Start new job
      </Button>

      <Modal visible={visible} title="Start a new job" okText="Start" cancelText="Cancel" onCancel={onCancel}
        onOk={() => {
          form.validateFields().then((values) => {
            form.resetFields();
            onCreate(values);
          }).catch((info) => {
            console.log('Validate Failed:', info);
          });
        }}>
        <Space  direction="vertical">
        <div className="popup-text">
          <InfoCircleTwoTone style={{ fontSize: '16px' }} /> You will be redirected to the run details page to see this job executed. 
        </div>
        <Form form={form} layout="vertical" name="create-org" validateMessages={validateMessages}>
          <Form.Item name="action" label="Choose job type" rules={[{ required: true }]}>
            <Select>
              <Select.Option value="plan">Plan</Select.Option>
              <Select.Option value="apply">Apply</Select.Option>
              <Select.Option value="destroy">Destroy</Select.Option>
            </Select>
          </Form.Item>
        </Form>
        </Space>
      </Modal>
    </div>
  )
}