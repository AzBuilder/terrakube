import { Alert, Breadcrumb, Button, Empty, Flex, Layout, Spin, Typography } from "antd";
import "antd/dist/reset.css";
import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { ORGANIZATION_ARCHIVE } from "../../config/actionTypes";
import organizationService from "@/modules/organizations/organizationService";
import { mapOrganization } from "./organizationMapper";
import useApiRequest from "@/modules/api/useApiRequest";
import { OrganizationModel } from "./types";
import "./OrganizationsPickerPage.css";
import OrganizationGrid from "./components/OrganizationGrid/OrganizationGrid";

const { Content } = Layout;

export default function OrganizationsPickerPage() {
  const [organizations, setOrganizations] = useState<OrganizationModel[]>([]);
  const navigate = useNavigate();
  const orgId = sessionStorage.getItem(ORGANIZATION_ARCHIVE);

  const { loading, execute, error } = useApiRequest({
    action: () => organizationService.listOrganizations(),
    onReturn: (data) => {
      const mapped = data.map(mapOrganization);
      setOrganizations(mapped);
    },
  });

  async function initPage() {
    if (orgId === "" || orgId === null) {
      await execute();
    } else {
      navigate(`/organizations/${orgId}/workspaces`, { replace: true });
    }
  }

  useEffect(() => {
    initPage();
  }, []);

  useEffect(() => {
    if (organizations.length === 1) {
      navigate(`/organizations/${organizations[0].id}/workspaces`, {
        replace: true,
      });
    }
  }, [organizations]);

  return (
    <Content style={{ padding: "0 50px" }}>
      <Breadcrumb style={{ margin: "16px 0" }}>
        <Breadcrumb.Item>Organizations</Breadcrumb.Item>
      </Breadcrumb>
      <div className="organization-picker-page">
        <div className="organization-picker-page-inner">
          <Typography.Title className="organization-picker-page-title">Choose an Organization</Typography.Title>
          <Typography.Text type="secondary">You have access to the following organizations</Typography.Text>

          {error && (
            <Alert
              className="organization-picker-page-alert"
              message="Failed to load organizations. Please try again later"
              type="error"
              showIcon
              banner
            />
          )}

          {loading && (
            <Flex align="center" className="organization-picker-page-loader" vertical gap="middle">
              <Spin tip="Loading" size="large" />
              <Typography.Text>Loading organizations...</Typography.Text>
            </Flex>
          )}
          {!loading && organizations.length === 0 && (
            <Flex justify="center">
              <Empty
                className="organization-picker-page-no-content"
                styles={{
                  description: {
                    textAlign: "center",
                  },
                }}
                description="You have not created any organizations yet. Create one now to get stared with Terrakube"
              >
                <Button color="purple" variant="filled">
                  <Link to="/organizations/create">Create a new organization</Link>
                </Button>
              </Empty>
            </Flex>
          )}
          {!loading && organizations.length > 0 && <OrganizationGrid organizations={organizations} />}
        </div>
      </div>
    </Content>
  );
}
