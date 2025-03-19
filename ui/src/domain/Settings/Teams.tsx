import { DeleteOutlined, EditOutlined, TeamOutlined } from "@ant-design/icons";
import { Avatar, Button, Divider, List, Popconfirm, Space } from "antd";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axiosInstance from "../../config/axiosConfig";
import { Team } from "../types";
import { EditTeam } from "./EditTeam";
import "./Settings.css";

type Props = {
  key: string;
};

export const TeamSettings = ({ key }: Props) => {
  const { orgid } = useParams();
  const [teams, setTeams] = useState<Team[]>([]);
  const [loading, setLoading] = useState(false);
  const [mode, setMode] = useState<"list" | "edit" | "create">("list");
  const [teamId, setTeamId] = useState<string>();

  const onEdit = (id: string) => {
    setMode("edit");
    setTeamId(id);
  };

  const onNew = () => {
    setMode("create");
  };

  const onDelete = (id: string) => {
    axiosInstance.delete(`organization/${orgid}/team/${id}`).then(() => {
      loadTeams();
    });
  };

  const loadTeams = () => {
    axiosInstance.get(`organization/${orgid}/team`).then((response) => {
      setTeams(response.data.data);
      setLoading(false);
    });
  };
  useEffect(() => {
    setLoading(true);
    loadTeams();
  }, [orgid, key]);

  return (
    <div className="setting">
      {(mode !== "list" && <EditTeam mode={mode} setMode={setMode} teamId={teamId} loadTeams={loadTeams} />) || (
        <>
          <h1>Team Management</h1>
          <div className="App-text">
            Teams let you group users into specific categories to enable finer grained access control policies. For
            example, your developers could be on a dev team that only has access to run jobs.
          </div>
          <Button type="primary" onClick={onNew} htmlType="button">
            Create team
          </Button>
          <br></br>

          <h3 style={{ marginTop: "30px" }}>Teams</h3>
          {loading || teams.length === 0 ? (
            <p>Data loading...</p>
          ) : (
            <List
              itemLayout="horizontal"
              dataSource={teams}
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
                    avatar={<Avatar style={{ backgroundColor: "#1890ff" }} icon={<TeamOutlined />}></Avatar>}
                    title={item.attributes.name}
                    description={
                      <div>
                        Access to Manage:&nbsp;&nbsp;&nbsp;
                        <Space split={<Divider type="vertical" />}>
                          {item.attributes.manageWorkspace ? <span>Workspaces</span> : null}
                          {item.attributes.manageState ? <span>State</span> : null}
                          {item.attributes.manageModule ? <span>Modules</span> : null}
                          {item.attributes.manageCollection ? <span>Collection</span> : null}
                          {item.attributes.manageJob ? <span>Job</span> : null}
                          {item.attributes.manageProvider ? <span>Providers</span> : null}
                          {item.attributes.manageVcs ? <span>Vcs</span> : null}
                          {item.attributes.manageTemplate ? <span>Templates</span> : null}
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
