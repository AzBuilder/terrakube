import { React, useState, useEffect } from "react";
import {EditTeam} from "./EditTeam";
import {
  Button,
  List,
  Popconfirm,
  Space,
  Avatar,
  Divider,
} from "antd";
import "./Settings.css";
import axiosInstance from "../../config/axiosConfig";
import { useParams } from "react-router-dom";
import {
  TeamOutlined,
  EditOutlined,
  DeleteOutlined,
} from "@ant-design/icons";

export const TeamSettings = ({key}) => {
  const { orgid } = useParams();
  const [teams, setTeams] = useState([]);
  const [loading, setLoading] = useState(false);
  const [visible, setVisible] = useState(false);

  const [mode, setMode] = useState("list");
  const [teamId, setTeamId] = useState([]);

  const onEdit = (id) => {
    setMode("edit");
    setTeamId(id);
    setVisible(true);

  };

  const onNew = () => {
    setMode("create");
  };

  const onDelete = (id) => {
    console.log("deleted " + id);
    axiosInstance
      .delete(`organization/${orgid}/team/${id}`)
      .then((response) => {
        console.log(response);
        loadTeams();
      });
  };

  const loadTeams = () => {
    axiosInstance.get(`organization/${orgid}/team`).then((response) => {
      console.log(response);
      setTeams(response.data);
      setLoading(false);
    });
  };
  useEffect(() => {
    setLoading(true);
    loadTeams();
  }, [orgid,key]);

  return (
    <div className="setting">
      {(mode !== "list" && <EditTeam mode={mode} setMode={setMode} teamId={teamId} loadTeams={loadTeams}/>) || (
        <>
          <h1>Team Management</h1>
          <div className="App-text">
            Teams let you group users into specific categories to enable finer
            grained access control policies. For example, your developers could
            be on a dev team that only has access to run jobs.
          </div>
          <Button type="primary" onClick={onNew} htmlType="button">
            Create team
          </Button>
          <br></br>

          <h3 style={{ marginTop: "30px" }}>Teams</h3>
          {loading || !teams.data ? (
            <p>Data loading...</p>
          ) : (
            <List
              itemLayout="horizontal"
              dataSource={teams.data}
              renderItem={(item) => (
                <List.Item
                  actions={[
                    <Button
                      onClick={() => {
                        onEdit(item.id);
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
                          This will permanently delete this team <br />
                          and any permissions associated with it. <br />
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
                  <List.Item.Meta
                    avatar={
                      <Avatar
                        style={{ backgroundColor: "#1890ff" }}
                        icon={<TeamOutlined />}
                      ></Avatar>
                    }
                    title={item.attributes.name}
                    description={
                      <div>
                        Access to Manage:&nbsp;&nbsp;&nbsp;
                        <Space split={<Divider type="vertical" />}>
                          {item.attributes.manageWorkspace ? (
                            <span>Workspaces</span>
                          ) : null}
                          {item.attributes.manageModule ? (
                            <span>Modules</span>
                          ) : null}
                          {item.attributes.manageProvider ? (
                            <span>Providers</span>
                          ) : null}
                          {item.attributes.manageVcs ? <span>Vcs</span> : null}
                          {item.attributes.manageTemplate ? (
                            <span>Templates</span>
                          ) : null}
                        </Space>
                      </div>
                    }
                  />
                </List.Item>
              )}
            />
          )}
        </>
      )}
    </div>
  );
};
