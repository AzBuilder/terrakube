import { ClockCircleOutlined } from "@ant-design/icons";
import { Card, Space, Row, Col, Typography, Flex } from "antd";
import { DateTime } from "luxon";
import { IconContext } from "react-icons";
import { BiTerminal } from "react-icons/bi";
import { WorkspaceListItem } from "@/modules/workspaces/types";
import getVcsNameFromUrl from "@/modules/workspaces/utils/getVcsNameFromUrl";
import getVcsTypeFromUrl from "@/modules/workspaces/utils/getVcsTypeFromUrl";
import VcsLogo from "@/modules/workspaces/components/VcsLogo";
import WorkspaceStatusTag from "@/modules/workspaces/components/WorkspaceStatusTag";
import WorkspaceCardTags from "@/modules/workspaces/components/WorkspaceCardTags";
import { TagModel } from "@/modules/organizations/types";
import IacTypeLogo from "./IacTypeLogo";

type Props = {
  item: WorkspaceListItem;
  tags: TagModel[];
};
export default function WorkspaceCard({ item, tags }: Props) {
  return (
    <Card hoverable>
      <Space style={{ color: "rgb(82, 87, 97)", width: "100%" }} direction="vertical">
        <Row>
          <Col span={12}>
            <Typography.Title level={3}>{item.name}</Typography.Title>
            {item.description || "No description provided for this workspace"}
          </Col>
          <Col span={12}>
            <Row justify="start">
              <Col span={24}>
                <Flex justify="end" wrap gap="small">
                  <WorkspaceCardTags tags={tags} item={item} />
                </Flex>
              </Col>
            </Row>
          </Col>
        </Row>
        <Space size={40} style={{ marginTop: "25px" }}>
          <Space>
            <WorkspaceStatusTag status={item.lastStatus} /> <br />
          </Space>
          <Space>
            <ClockCircleOutlined />
            {item.lastRun ? DateTime.fromISO(item.lastRun).toRelative() : "Never Executed"}
          </Space>
          <Space>
            <IacTypeLogo type={item.iacType} />
            {item.terraformVersion}
          </Space>
          {item.branch !== "remote-content" && item.source ? (
            <Space>
              <VcsLogo type={getVcsTypeFromUrl(item.source)} />
              <a href={item.normalizedSource} target="_blank" rel="noreferrer">
                {item.normalizedSource ? getVcsNameFromUrl(item.normalizedSource) : "Unknown"}
              </a>
            </Space>
          ) : (
            <span
              style={{
                verticalAlign: "middle",
                display: "inline-block",
              }}
            >
              <IconContext.Provider value={{ size: "1.4em" }}>
                <BiTerminal />
              </IconContext.Provider>
              &nbsp;&nbsp;cli/api driven workflow
            </span>
          )}
        </Space>
      </Space>
    </Card>
  );
}
