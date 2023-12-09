import { React } from "react";
import {
  Drawer,
  Avatar,
  Descriptions,
  Typography,
  Collapse,
  Space,
  Button,
  Tag
} from "antd";
import { QuestionCircleOutlined } from "@ant-design/icons";
import { getServiceIcon } from "./Icons.js";
const { Panel } = Collapse;
const { Paragraph } = Typography;
export const ResourceDrawer = ({ open, resource, setOpen }) => {
  const onClose = () => {
    setOpen(false);
  };
  return (
    <Drawer
      width={640}
      title={
        <>
          <Avatar
            shape="square"
            size="small"
            src={getServiceIcon(resource.provider, resource.type)}
          />{" "}
          {resource.name}
        </>
      }
      placement="right"
      onClose={onClose}
      open={open}
    >
      <Space size={10} style={{ width: "100%" }} direction="vertical">
        <Space direction="horizontal">
          <Button target="_blank" href={"https://registry.terraform.io/providers/" + resource.provider?.split('/')?.slice(-2)?.join('/')  + "/latest/docs/resources/" + resource?.type?.split('_')?.slice(1)?.join('_')} icon={<QuestionCircleOutlined />}>Open documentation</Button>
        </Space>

        <Collapse defaultActiveKey={["1"]}>
          <Panel header={<h4>Attributes</h4>} key="1">
            <Descriptions bordered column={1}>
              {Object.entries(resource?.values || {}).map(([key, value]) => {
                return (
                  <Descriptions.Item label={key}>
                    <Paragraph
                      style={{ margin: "0px" }}
                      copyable={value !== null ? { tooltips: false } : false}
                    >
                      {value === null
                        ? ""
                        : typeof value === "object"
                        ? JSON.stringify(value, null, 2)
                        : value}
                    </Paragraph>
                  </Descriptions.Item>
                );
              })}
            </Descriptions>
          </Panel>
        </Collapse>
        <Collapse style={{display: resource?.depends_on?.length > 0 ? "block":"none"}} defaultActiveKey={["2"]}>
          <Panel header={<h4>Depends on</h4>} key="2">
            <br/>
          <ul>
              {resource?.depends_on?.map((dependency) => {
                return <li><Tag>{dependency}</Tag></li>;
              })}
            </ul>
          </Panel>

          
        </Collapse>
      </Space>
    </Drawer>
  );
};
