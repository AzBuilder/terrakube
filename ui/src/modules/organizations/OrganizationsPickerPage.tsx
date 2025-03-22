import { Button, Empty, Flex } from "antd";
import "antd/dist/reset.css";
import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { ORGANIZATION_ARCHIVE } from "../../config/actionTypes";
import organizationService from "@/modules/organizations/organizationService";
import { mapOrganization } from "./organizationMapper";
import useApiRequest from "@/modules/api/useApiRequest";
import { OrganizationModel } from "./types";
import OrganizationGrid from "./components/OrganizationGrid/OrganizationGrid";
import PageWrapper from "@/modules/layout/PageWrapper/PageWrapper";


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
    <PageWrapper
      title="Choose an Organization"
      subTitle="You have access to the following organizations"
      error={error}
      loading={loading}
      loadingText="Loading organizations..."
      breadcrumbs={[{ label: "Organizations", path: "/" }]}
    >
      {!loading && organizations.length === 0 && (
        <Flex justify="center">
          <Empty
            className="page-wrapper-no-content"
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
    </PageWrapper>
  );
}
