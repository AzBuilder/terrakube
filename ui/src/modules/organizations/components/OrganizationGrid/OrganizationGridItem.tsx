import { GoOrganization } from "react-icons/go";
import { Link } from "react-router-dom";
import { Flex, Typography, Card } from "antd";
import stringToDeterministicColor from "@/modules/utils/stringToDeterministicColor";
import { OrganizationModel } from "../../types";

type Props = {
  organization: OrganizationModel;
};

export default function OrganizationGridItem({ organization }: Props) {
  return (
    <Link to={`/organizations/${organization.id}/workspaces`}>
      <Card hoverable style={{ width: "100%" }}>
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
      </Card>
    </Link>
  );
}
