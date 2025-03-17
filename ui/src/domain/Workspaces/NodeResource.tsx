import { Avatar, Button, Card } from "antd";
import { memo } from "react";
import { HiOutlineExternalLink } from "react-icons/hi";
import { Handle, NodeProps, Position } from "reactflow";
import { Resource } from "../types.js";
import { getServiceIcon } from "./Icons.jsx";
const { Meta } = Card;

export default memo(({ data, isConnectable }: NodeProps<Resource>) => {
  return (
    <>
      <Handle
        type="source"
        position={Position.Top}
        style={{ background: "#555" }}
        onConnect={(params) => console.log("handle onConnect", params)}
        isConnectable={isConnectable}
      />
      <Card style={{ width: 300 }}>
        <Meta
          avatar={GetResourceIcon(data)}
          title={
            <>
              {" "}
              <Button onClick={() => data.showDrawer(data)} type="link">
                {data.name} &nbsp;
                <HiOutlineExternalLink />
              </Button>{" "}
            </>
          }
          description={data.type}
        />
      </Card>
      <Handle
        type="target"
        position={Position.Bottom}
        id="b"
        style={{ background: "#555" }}
        isConnectable={isConnectable}
      />
    </>
  );
});

const GetResourceIcon = (resource: Resource) => {
  const iconSource = getServiceIcon(resource.provider, resource.type);
  return <Avatar shape="square" size="large" src={iconSource} />;
};
