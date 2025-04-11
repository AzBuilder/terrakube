import { Avatar, Tag, Typography } from "antd";
import { UserOutlined } from "@ant-design/icons";
import {
  CheckCircleOutlined,
  ClockCircleOutlined,
  ExclamationCircleOutlined,
  StopOutlined,
  SyncOutlined,
} from "@ant-design/icons";
import { FlatJob, JobStatus } from "../../../domain/types";

type Props = {
  item: FlatJob;
  onClick: (id: string) => void;
};

export default function RunCard({ item, onClick }: Props) {
  return (
    <div>
      <div className="textLeft">
        <Tag
          icon={
            item.status === JobStatus.Completed ? (
              <CheckCircleOutlined />
            ) : item.status === JobStatus.NoChanges ? (
              <CheckCircleOutlined />
            ) : item.status === JobStatus.Running ? (
              <SyncOutlined spin />
            ) : item.status === JobStatus.WaitingApproval ? (
              <ExclamationCircleOutlined />
            ) : item.status === JobStatus.Cancelled ? (
              <StopOutlined />
            ) : item.status === JobStatus.Failed ? (
              <StopOutlined />
            ) : (
              <ClockCircleOutlined />
            )
          }
          color={item.statusColor}
        >
          {item.status}
        </Tag>{" "}
        <br />
        <span className="metadata">{item.latestChange}</span>
      </div>
      <div>
        <Avatar shape="square" icon={<UserOutlined />} />
        <Typography.Link onClick={() => onClick(item.id)}>{item.title}</Typography.Link>
        <Typography.Text type="secondary">
          {" "}
          #job-{item.id} |
          {item.commitId !== "000000000" ? <> commitId {item.commitId?.substring(0, 6)} </> : ""}|{" "}
          <b>{item.createdBy}</b> triggered via {item.via || "UI"}
        </Typography.Text>
      </div>
    </div>
  );
} 