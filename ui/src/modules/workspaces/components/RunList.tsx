import { List, Avatar, Tag, Pagination, Tooltip } from "antd";
import { UserOutlined } from "@ant-design/icons";
import {
  CheckCircleOutlined,
  ClockCircleOutlined,
  ExclamationCircleOutlined,
  StopOutlined,
  SyncOutlined,
} from "@ant-design/icons";
import { FlatJob, JobStatus } from "../../../domain/types";
import { useState } from "react";

// Helper function to format date
const formatDate = (dateString?: string) => {
  if (!dateString) return '';
  try {
    const date = new Date(dateString);
    return date.toLocaleString();
  } catch (e) {
    return dateString;
  }
};

type Props = {
  jobs: FlatJob[];
  onRunClick: (id: string) => void;
};

export default function RunList({ jobs, onRunClick }: Props) {
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 10;

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

  const sortedJobs = jobs.sort((a, b) => parseInt(a.id) - parseInt(b.id)).reverse();
  const paginatedJobs = sortedJobs.slice((currentPage - 1) * pageSize, currentPage * pageSize);
  
  // Find the job with highest ID to mark as current
  const highestId = sortedJobs.length > 0 ? sortedJobs[0].id : "-1";

  return (
    <div>
      <h3>Run List</h3>
      <List
        itemLayout="horizontal"
        dataSource={paginatedJobs}
        renderItem={(item) => (
          <List.Item
            actions={[
              <div key="status" style={{ textAlign: 'right' }}>
                {renderStatusTag(item.status, item.statusColor)}
                <div>
                  <Tooltip title={formatDate((item as any).createdDate)}>
                    <span className="metadata">{item.latestChange}</span>
                  </Tooltip>
                </div>
              </div>
            ]}
          >
            <List.Item.Meta
              avatar={<Avatar shape="square" icon={<UserOutlined />} />}
              title={
                <span>
                  <a onClick={() => onRunClick(item.id)} style={{ color: 'inherit' }}>{item.title}</a>
                  {item.id === highestId && (
                    <Tag style={{ marginLeft: 8 }}>CURRENT</Tag>
                  )}
                </span>
              }
              description={
                <span>
                  #job-{item.id} &nbsp;&nbsp;|&nbsp;&nbsp; <b>{item.createdBy}</b> triggered via <b>{item.via || "UI"}</b> &nbsp;&nbsp;|&nbsp;&nbsp; <a>#{item.commitId?.substring(0, 6)}</a>
                </span>
              }
            />
          </List.Item>
        )}
        pagination={false}
      />
      {sortedJobs.length > 0 && (
        <div style={{ textAlign: 'right', marginTop: '16px' }}>
          <Pagination
            current={currentPage}
            pageSize={pageSize}
            total={sortedJobs.length}
            onChange={setCurrentPage}
            showSizeChanger={false}
          />
        </div>
      )}
    </div>
  );
} 