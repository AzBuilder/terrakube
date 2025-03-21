import { Flex, Typography } from "antd";
import { OrganizationModel } from "@/modules/organizations/types";
import { GoOrganization } from "react-icons/go";
import { Link } from "react-router-dom";
import stringToDeterministicColor from "@/modules/utils/stringToDeterministicColor";

type Props = {
  organization: OrganizationModel;
};

export default function OrganizationGridItem({ organization }: Props) {
  return (
    <Link to={`/organizations/${organization.id}/workspaces`} className="org-card">
      <Flex gap="small" align="center">
        <div className="org-card-icon">
          <GoOrganization style={{ color: stringToDeterministicColor(organization.id) }} />
        </div>
        <Flex vertical gap="0">
          <Typography.Text className="org-card-title" ellipsis>
            {organization.name}
          </Typography.Text>
          <Typography.Text type="secondary">
            {organization.description || "No description set for this organization"}
          </Typography.Text>
        </Flex>
      </Flex>
    </Link>
  );
}
