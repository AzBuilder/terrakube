import { DeleteOutlined, EditOutlined } from "@ant-design/icons";
import { Button, List, Popconfirm } from "antd";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axiosInstance from "../../config/axiosConfig";
import { Template } from "../types";
import { AddTemplate } from "./AddTemplate";
import { EditTemplate } from "./EditTemplate";
import "./Settings.css";

type Props = {
  key: string;
};

export const TemplatesSettings = ({ key }: Props) => {
  const { orgid } = useParams();
  const [loading, setLoading] = useState(false);
  const [mode, setMode] = useState("list");
  const [templates, setTemplates] = useState<Template[]>([]);
  const [templateID, setTemplateID] = useState<string>();

  const onAddVCS = () => {
    setMode("new");
  };

  const onEditVCS = (id: string) => {
    setMode("edit");
    setTemplateID(id);
  };

  const onDelete = (id: string) => {
    axiosInstance.delete(`organization/${orgid}/template/${id}`).then(() => {
      loadTemplates();
    });
  };

  useEffect(() => {
    setLoading(true);
    loadTemplates();
  }, [orgid, templateID, key]);

  const loadTemplates = () => {
    axiosInstance.get(`organization/${orgid}/template`).then((response) => {
      setTemplates(response.data.data);
      setLoading(false);
    });
  };

  return (
    <div className="setting">
      {(mode === "new" && <AddTemplate setMode={setMode} loadTemplates={loadTemplates} />) ||
        (mode === "edit" && (
          <EditTemplate setMode={setMode} templateId={templateID} loadTemplates={loadTemplates} />
        )) || (
          <div>
            {" "}
            <h1 style={{ paddingBottom: "10px" }}>
              Templates
              <Button type="primary" onClick={onAddVCS} className="addVCS" htmlType="button">
                Add a Template
              </Button>{" "}
            </h1>
            <br />
            {loading ? (
              <p>Data loading...</p>
            ) : (
              <List
                className="vcsList"
                itemLayout="horizontal"
                dataSource={templates}
                renderItem={(item) => (
                  <List.Item
                    actions={[
                      <Button
                        onClick={() => {
                          onEditVCS(item.id);
                        }}
                        icon={<EditOutlined />}
                        type="link"
                      >
                        Edit
                      </Button>,
                      <Popconfirm
                        onConfirm={() => {
                          onDelete(item.id);
                        }}
                        style={{ width: "20px" }}
                        title={
                          <p>
                            This will permanently delete this template. <br />
                            Are you sure?
                          </p>
                        }
                        okText="Yes"
                        cancelText="No"
                      >
                        {" "}
                        <Button icon={<DeleteOutlined />} type="link" danger>
                          Delete
                        </Button>
                      </Popconfirm>,
                    ]}
                  >
                    <List.Item.Meta title={item.attributes.name} description={item.attributes.description} />
                  </List.Item>
                )}
              />
            )}
          </div>
        )}
    </div>
  );
};
