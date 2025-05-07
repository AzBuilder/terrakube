import { Link } from "react-router-dom";
import { Flex, Typography, Card } from "antd";
import stringToDeterministicColor from "@/modules/utils/stringToDeterministicColor";
import { OrganizationModel } from "../../types";
import * as FaIcons from "react-icons/fa6";

const DEFAULT_ICON = "FaBuilding";
const DEFAULT_COLOR = "#000000";

// Helper to parse icon field and get icon name and color
function parseIconField(iconField: string | undefined, orgId: string): { iconName: string; color: string } {
  if (!iconField) {
    // If icon field is empty, use FaBuilding and deterministic color
    return { iconName: DEFAULT_ICON, color: stringToDeterministicColor(orgId) };
  }
  const [iconName, color] = iconField.split(":");
  return {
    iconName: iconName || DEFAULT_ICON,
    // If color is missing, use black; if icon field is empty, handled above
    color: color ? color : DEFAULT_COLOR,
  };
}

// Helper to get the icon component
function getOrgIcon(iconName: string, color: string) {
  const IconComponent = FaIcons[(iconName as keyof typeof FaIcons) || DEFAULT_ICON] || FaIcons[DEFAULT_ICON];
  return <IconComponent style={{ color, fontSize: 40 }} />;
}

type Props = {
  organization: OrganizationModel;
};

export default function OrganizationGridItem({ organization }: Props) {
  const { iconName, color } = parseIconField(organization.icon, organization.id);
  return (
    <Link to={`/organizations/${organization.id}/workspaces`}>
      <Card hoverable style={{ width: "100%" }}>
        <Flex gap="small" align="center">
          <div className="org-card-icon">{getOrgIcon(iconName, color)}</div>
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
