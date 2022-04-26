import { React, useState, useEffect } from "react";
import './Settings.css';
import { Button, List,Popconfirm} from "antd";
import { AddTemplate } from "./AddTemplate"
import { EditTemplate } from "./EditTemplate"
import { useParams } from "react-router-dom";
import { DeleteOutlined,EditOutlined} from '@ant-design/icons';
import axiosInstance from "../../config/axiosConfig";

export const TemplatesSettings = ({key}) => {
  const { orgid} = useParams();
  const [loading, setLoading] = useState(false);
  const [mode, setMode] = useState("list");
  const [templates, setTemplates] = useState([]);
  const [templateID, setTemplateID] = useState([]);

  const onAddVCS = () => {
    setMode("new");
  };

  const onEditVCS = (id) => {
    setMode("edit");
    setTemplateID(id);
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
  }, [orgid,templateID,key]);

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
      {(mode === "new" &&  <AddTemplate setMode={setMode} loadTemplates={loadTemplates} />) 
      || (mode === "edit" &&  <EditTemplate setMode={setMode} templateId={templateID} loadTemplates={loadTemplates}/>) 
      ||(
        <div> <h1 style={{ paddingBottom: "10px" }}>Templates 
          <Button type="primary" onClick={onAddVCS} className="addVCS" htmlType="button">Add a Template</Button> </h1><br />
          {loading || !templates.data ? (
            <p>Data loading...</p>
          ) : (
            <List className="vcsList" itemLayout="horizontal" dataSource={templates.data}
              renderItem={item => (
                <List.Item  actions={[<Button onClick={() => {
                  onEditVCS(item.id);
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
      )}
    </div>
  );
}