import { Card, Space, Row, Col, Typography, Flex } from "antd";
import { IconContext } from "react-icons";
import { BiTerminal } from "react-icons/bi";
import { StackListItem } from "@/modules/stacks/types";
import getVcsNameFromUrl from "@/modules/workspaces/utils/getVcsNameFromUrl";
import getVcsTypeFromUrl from "@/modules/workspaces/utils/getVcsTypeFromUrl";
import VcsLogo from "@/modules/workspaces/components/VcsLogo";
import IacTypeLogo from "@/modules/workspaces/components/IacTypeLogo";
import { FaServer } from "react-icons/fa";

type Props = {
  item: StackListItem;
};

export default function StackCard({ item }: Props) {
  return (
    <Card hoverable>
      <Space style={{ color: "rgb(82, 87, 97)", width: "100%" }} direction="vertical">
        <Row>
          <Col span={12}>
            <Typography.Title level={3}>{item.name}</Typography.Title>
            {item.description || "No description provided for this stack"}
          </Col>
          <Col span={12}>
            <Row justify="start">
              <Col span={24}>
                <Flex justify="end" wrap="wrap" gap="small">
                  <div></div>
                </Flex>
              </Col>
            </Row>
          </Col>
        </Row>
        <Space size={40} style={{ marginTop: "25px" }}>
          <Space>
            <FaServer />
            <span>2 deployments: dev, prod</span>
          </Space>
          <Space>
            <IacTypeLogo type={item.iacEngine} />
            {item.toolType}
          </Space>
          {item.repoUrl ? (
            <Space>
              <VcsLogo type={getVcsTypeFromUrl(item.repoUrl)} />
              <a href={item.repoUrl} target="_blank" rel="noreferrer">
                {getVcsNameFromUrl(item.repoUrl)}
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