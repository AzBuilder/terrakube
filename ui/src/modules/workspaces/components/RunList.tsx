import { List } from "antd";
import { FlatJob } from "../../../domain/types";
import RunCard from "./RunCard";

type Props = {
  jobs: FlatJob[];
  onRunClick: (id: string) => void;
};

export default function RunList({ jobs, onRunClick }: Props) {
  return (
    <div>
      <h3>Run List</h3>
      <List
        itemLayout="horizontal"
        dataSource={jobs.sort((a, b) => parseInt(a.id) - parseInt(b.id)).reverse()}
        renderItem={(item) => (
          <List.Item>
            <RunCard item={item} onClick={onRunClick} />
          </List.Item>
        )}
      />
    </div>
  );
} 