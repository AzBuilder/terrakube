import { React, useState, useEffect } from "react";
import './Settings.css';
import { Button, List, Card ,Divider,Row,Col,Popconfirm} from "antd";
import { GithubOutlined, GitlabOutlined } from '@ant-design/icons';
import { AddTemplate } from "./AddTemplate"
import { useParams } from "react-router-dom";
import { DeleteOutlined,EditOutlined} from '@ant-design/icons';
import { SiBitbucket, SiAzuredevops } from "react-icons/si";
import axiosInstance from "../../config/axiosConfig";
import { IconContext } from "react-icons";
import {ORGANIZATION_NAME } from '../../config/actionTypes';

export const TemplatesSettings = ({vcsMode}) => {
  const { orgid} = useParams();
  const [loading, setLoading] = useState(false);
  const [mode, setMode] = useState(vcsMode!=null?vcsMode:"list");
  const [templates, setTemplates] = useState([]);

  const onAddVCS = () => {
    setMode("new");
  };

  const onDelete = (id) => {
    console.log("deleted " + id);
    axiosInstance.delete(`organization/${orgid}/template/${id}`)
    .then(response => {
      console.log(response);
      loadTemplates();
    });
  };


  useEffect(() => {
    setLoading(true);
    loadTemplates();

  }, [orgid]);

  const loadTemplates = () => {
    axiosInstance.get(`organization/${orgid}/template`)
      .then(response => {
        console.log(response);
        setTemplates(response.data);
        setLoading(false);
      });
  }

  return (
    <div className="setting">
      {mode != "new" ? (
        <div> <h1 style={{ paddingBottom: "10px" }}>Templates 
          <Button type="primary" onClick={onAddVCS} className="addVCS" htmlType="button">Add a Template</Button> </h1><br />
          {loading || !templates.data ? (
            <p>Data loading...</p>
          ) : (
            <List className="vcsList" itemLayout="horizontal" dataSource={templates.data}
              renderItem={item => (
                <List.Item  actions={[<Button onClick={() => {
                  
                }} icon={<EditOutlined />} type="link">Edit</Button>, <Popconfirm onConfirm={() => {
                  onDelete(item.id);
                }} style={{width:"20px"}} title={<p>This will permanently delete this template. <br/>
                Are you sure?</p>} okText="Yes" cancelText="No"> <Button icon={<DeleteOutlined />} type="link" danger>Delete</Button></Popconfirm>]}> 
                <List.Item.Meta
                      title={item.attributes.name}
                      description={item.attributes.description}/>
                </List.Item>
              )}
            />
          )}
        </div>
      ) : (
        <AddTemplate setMode={setMode} loadTemplates={loadTemplates} />
      )}
    </div>
  );
}