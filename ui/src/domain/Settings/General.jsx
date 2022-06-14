import { React, useState, useEffect } from "react";
import { Button, Form,Input,message,Spin} from "antd";
import axiosInstance from "../../config/axiosConfig";
import {useParams} from "react-router-dom";
import './Settings.css';

export const GeneralSettings = () => {
  const { orgid } = useParams();
  const [organization, setOrganization] = useState([]);
  const [loading, setLoading] = useState(false);
  const [waiting, setWaiting] = useState(false);
  useEffect(() => {
    setLoading(true);
    axiosInstance.get(`organization/${orgid}`)
      .then(response => {
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
        }
      }
    }
    console.log(body);

    axiosInstance.patch(`organization/${orgid}`, body, {
      headers: {
        'Content-Type': 'application/vnd.api+json'
      }
    })
      .then(response => {
        console.log(response);
        if (response.status == "204") {
          message.success('Organization updated successfully');

        }
        else{
          message.error('Organization update failed');
        }
        setWaiting(false);

      })
  };
  
  return (
    <div className="setting" >
      <h1>General Settings</h1>
      {loading || !organization.data ? (
          <p>Data loading...</p>
        ) : (
          <Spin spinning={waiting}>
      <Form onFinish={onFinish} layout="vertical" name="form-settings" >
        <Form.Item name="name" label="Name" >
          <Input defaultValue={organization.data.attributes.name} />
        </Form.Item>
        <Form.Item name="description" label="Description" >
          <Input.TextArea  defaultValue={organization.data.attributes.description}/>
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit">
            Update organization
          </Button>
        </Form.Item>
      </Form></Spin>)}
      <h1>Delete this Organization</h1>
      <div className="App-text">
        Deleting the devops-mindset organization will permanently delete all workspaces associated with it. Please be certain that you understand this. This action cannot be undone.
      </div>
      <Button type="primary" danger htmlType="submit">
        Delete this organization
      </Button>
    </div>
  );
}