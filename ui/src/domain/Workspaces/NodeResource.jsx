import React, { memo } from "react";
import { Card, Avatar } from "antd";
import { Handle } from "react-flow-renderer";
import { getServiceIcon } from "./Icons.js";
const { Meta } = Card;

export default memo(({ data, isConnectable }) => {
  return (
    <>
      <Handle
        type="source"
        position="top"
        style={{ background: "#555" }}
        onConnect={(params) => console.log("handle onConnect", params)}
        isConnectable={isConnectable}
      />
      <Card style={{ width: 300 }}>
        <Meta
          avatar={GetResourceIcon(data)}
          title={data.name}
          description={data.type}
        />
      </Card>
      <Handle
        type="target"
        position="bottom"
        id="b"
        style={{ background: "#555" }}
        isConnectable={isConnectable}
      />
    </>
  );
});

const GetResourceIcon = (resource) => {
  const iconSource = getServiceIcon(resource.provider, resource.type);
  return <Avatar shape="square" size="large" src={iconSource} />;
};
