import { React, useState, useEffect } from "react";
import "antd/dist/antd.css";
import "./Home.css";
import axiosInstance from "../../config/axiosConfig";
import { Layout, Breadcrumb, List, Card } from "antd";
import { useHistory } from "react-router-dom";
import { ORGANIZATION_ARCHIVE } from "../../config/actionTypes";
const { Content } = Layout;
export const Home = () => {
  const [organizations, setOrganizations] = useState([]);
  const history = useHistory();
  const orgId = localStorage.getItem(ORGANIZATION_ARCHIVE);
  useEffect(() => {
    if (orgId === "" || orgId === null) {
      axiosInstance.get("organization").then((response) => {
        var orgs = response.data?.data;
        setOrganizations(orgs);
        if (orgs?.length === 1) {
          history.push(`/organizations/${orgs[0].id}/workspaces`);
          history.go(0);
        }
      });
    } else {
      history.push(`/organizations/${orgId}/workspaces`);
      history.go(0);
    }
  });

  const handleClick = (id) => {
    history.push(`/organizations/${id}/workspaces`);
    history.go(0);
  };

  return (
    <Content style={{ padding: "0 50px" }}>
      <Breadcrumb style={{ margin: "16px 0" }}>
        <Breadcrumb.Item>Organizations</Breadcrumb.Item>
      </Breadcrumb>
      <div className="site-layout-content">
        <div className="createOrganization">
          <h1>Choose an Organization</h1>
          <div className="App-text">
            You have access to the following organizations
          </div>
          <List
            split=""
            dataSource={organizations}
            renderItem={(item) => (
              <List.Item>
                <Card
                  onClick={() => handleClick(item.id)}
                  style={{ width: "100%" }}
                  hoverable
                >
                  {item.attributes.name}
                </Card>
              </List.Item>
            )}
          />
        </div>
      </div>
    </Content>
  );
};
