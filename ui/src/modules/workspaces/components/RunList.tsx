import { List, Avatar, Tag } from "antd";
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
  jobs: FlatJob[];
  onRunClick: (id: string) => void;
};

export default function RunList({ jobs, onRunClick }: Props) {
  const renderStatusTag = (status: JobStatus, statusColor: string) => (
    <Tag
      icon={
        status === JobStatus.Completed ? (
          <CheckCircleOutlined />
        ) : status === JobStatus.NoChanges ? (
          <CheckCircleOutlined />
        ) : status === JobStatus.Running ? (
          <SyncOutlined spin />
        ) : status === JobStatus.WaitingApproval ? (
          <ExclamationCircleOutlined />
        ) : status === JobStatus.Cancelled ? (
          <StopOutlined />
        ) : status === JobStatus.Failed ? (
          <StopOutlined />
        ) : (
          <ClockCircleOutlined />
        )
      }
      color={statusColor}
    >
      {status.toLowerCase()}
    </Tag>
  );

  return (
    <div>
      <h3>Run List</h3>
      <List
        itemLayout="horizontal"
        dataSource={jobs.sort((a, b) => parseInt(a.id) - parseInt(b.id)).reverse()}
        renderItem={(item) => (
          <List.Item
            actions={[
              <div key="status" style={{ textAlign: 'right' }}>
                {renderStatusTag(item.status, item.statusColor)}
                <div>
                  <span className="metadata">{item.latestChange}</span>
                </div>
              </div>
            ]}
          >
            <List.Item.Meta
              avatar={<Avatar shape="square" icon={<UserOutlined />} />}
              title={<a onClick={() => onRunClick(item.id)}>{item.title}</a>}
              description={
                <span>
                  #job-{item.id} | commitId {item.commitId?.substring(0, 6)} | {item.createdBy} triggered via {item.via || "UI"}
                </span>
              }
            />
          </List.Item>
        )}
      />
    </div>
  );
} 