import { React, useState, useEffect } from "react";
import { Button, Form, Input} from "antd";
import axiosInstance from "../../config/axiosConfig";
import {useParams} from "react-router-dom";
import './Settings.css';

export const GeneralSettings = () => {
  const { orgid } = useParams();
  const [organization, setOrganization] = useState([]);
  const [loading, setLoading] = useState(false);
  useEffect(() => {
    setLoading(true);
    axiosInstance.get(`organization/${orgid}`)
      .then(response => {
        console.log(response);
        setOrganization(response.data);
        setLoading(false);
      });

  }, [orgid]);
  return (
    <div className="setting" >
      <h1>General Settings</h1>
      {loading || !organization.data ? (
          <p>Data loading...</p>
        ) : (
      <Form layout="vertical" name="form-settings" >
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
      </Form>)}
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