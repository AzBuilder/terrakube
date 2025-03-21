import { Row, Col } from "antd";
import OrganizationGridItem from "./OrganizationGridItem";
import "./OrganizationGrid.css";
import { OrganizationModel } from "@/modules/organizations/types";

type Props = {
  organizations: OrganizationModel[];
};

export default function OrganizationGrid({ organizations }: Props) {
  return (
    <div className="organization-grid">
      <Row wrap={true} gutter={[8, 8]}>
        {organizations.map((org) => (
          <Col xxl={12} md={24} key={org.id}>
            <OrganizationGridItem organization={org} />
          </Col>
        ))}
      </Row>
    </div>
  );
}
