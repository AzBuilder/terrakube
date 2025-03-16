import { Breadcrumb, Card, Layout, List } from "antd";
import "antd/dist/reset.css";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { ORGANIZATION_ARCHIVE } from "../../config/actionTypes";
import axiosInstance from "../../config/axiosConfig";
import { Organization } from "../types";
import "./Home.css";

const { Content } = Layout;

export const Home = () => {
  const [organizations, setOrganizations] = useState<Organization[]>([]);
  const navigate = useNavigate();
  const orgId = sessionStorage.getItem(ORGANIZATION_ARCHIVE);

  useEffect(() => {
    if (orgId === "" || orgId === null) {
      axiosInstance.get("organization").then((response) => {
        var orgs = response.data?.data;
        setOrganizations(orgs);
        if (orgs?.length === 1) {
          navigate(`/organizations/${orgs[0].id}/workspaces`, {
            replace: true,
          });
        }
      });
    } else {
      navigate(`/organizations/${orgId}/workspaces`, { replace: true });
    }
  }, [orgId, navigate]);

  const handleClick = (id: string) => {
    navigate(`/organizations/${id}/workspaces`, { replace: true });
  };

  return (
    <Content style={{ padding: "0 50px" }}>
      <Breadcrumb style={{ margin: "16px 0" }}>
        <Breadcrumb.Item>Organizations</Breadcrumb.Item>
      </Breadcrumb>
      <div className="site-layout-content">
        <div className="createOrganization">
          <h1>Choose an Organization</h1>
          <div className="App-text">You have access to the following organizations</div>
          <List
            split
            dataSource={organizations}
            renderItem={(item) => (
              <List.Item>
                <Card onClick={() => handleClick(item.id)} style={{ width: "100%" }} hoverable>
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
