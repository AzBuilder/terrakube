import {
  BarsOutlined,
  ExclamationCircleOutlined,
  StopOutlined,
  SyncOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  PauseCircleOutlined,
} from "@ant-design/icons";
import { Card, Row, Col, Segmented, theme, Select, Flex } from "antd";
import { FlatJob, JobStatus } from "../../../domain/types";
import { useEffect, useState, useMemo, ReactNode } from "react";

type Props = {
  jobs: FlatJob[];
  onFiltered: (jobs: FlatJob[]) => void;
  applyFilter: (jobs: FlatJob[], filter: string) => FlatJob[];
  templateNames: { [key: string]: string };
};

type StatusCount = {
  [key: string]: number;
};

type StatusIconMap = {
  [key in JobStatus | "All"]: ReactNode;
};

// Storage keys for persisting filter state
const RUNS_FILTER_KEY = "runsFilterValue";
const RUNS_TEMPLATE_FILTER_KEY = "runsTemplateFilter";

// Map status to their icons and colors
const statusIcons: StatusIconMap = {
  [JobStatus.Completed]: <CheckCircleOutlined style={{ color: "#2eb039" }} />,
  [JobStatus.NoChanges]: <CheckCircleOutlined style={{ color: "#2eb039" }} />,
  [JobStatus.Running]: <SyncOutlined style={{ color: "#108ee9" }} />,
  [JobStatus.WaitingApproval]: <ExclamationCircleOutlined style={{ color: "#fa8f37" }} />,
  [JobStatus.Pending]: <PauseCircleOutlined style={{ color: "#808080" }} />,
  [JobStatus.Cancelled]: <StopOutlined style={{ color: "#FB0136" }} />,
  [JobStatus.Failed]: <StopOutlined style={{ color: "#FB0136" }} />,
  [JobStatus.Approved]: <CheckCircleOutlined style={{ color: "#2eb039" }} />,
  [JobStatus.Queue]: <ClockCircleOutlined style={{ color: "#808080" }} />,
  [JobStatus.Rejected]: <StopOutlined style={{ color: "#FB0136" }} />,
  [JobStatus.Unknown]: <ExclamationCircleOutlined style={{ color: "#808080" }} />,
  [JobStatus.NotExecuted]: <ClockCircleOutlined style={{ color: "#808080" }} />,
  All: <BarsOutlined />,
};

// Helper function to capitalize first letter
const capitalize = (str: string): string => {
  return str.charAt(0).toUpperCase() + str.slice(1);
};

// Safely parse JSON with a fallback value
const safeJsonParse = (jsonString: string | null, fallback: any): any => {
  if (!jsonString) return fallback;

  try {
    return JSON.parse(jsonString);
  } catch (e) {
    console.warn("Failed to parse JSON:", e);
    return fallback;
  }
};

// Statuses to always show in the filter
const alwaysShowStatuses = [
  "All",
  JobStatus.WaitingApproval,
  JobStatus.Failed,
  JobStatus.Running,
  JobStatus.Pending,
  JobStatus.Completed,
];

export default function RunFilter({ jobs, onFiltered, applyFilter, templateNames }: Props) {
  const {
    token: { colorBgContainer },
  } = theme.useToken();

  const [statusFilter, setStatusFilter] = useState<string>(sessionStorage.getItem(RUNS_FILTER_KEY) || "All");
  const [templateFilters, setTemplateFilters] = useState<string[]>(
    safeJsonParse(sessionStorage.getItem(RUNS_TEMPLATE_FILTER_KEY), [])
  );
  const [statusCounts, setStatusCounts] = useState<StatusCount>({});

  // Save filter values to session storage when they change
  useEffect(() => {
    sessionStorage.setItem(RUNS_FILTER_KEY, statusFilter);
  }, [statusFilter]);

  useEffect(() => {
    sessionStorage.setItem(RUNS_TEMPLATE_FILTER_KEY, JSON.stringify(templateFilters));
  }, [templateFilters]);

  // Get template options for the dropdown
  const templateOptions = useMemo(() => {
    const uniqueTemplates = new Set<string>();

    jobs.forEach((job) => {
      const templateId = (job as any).templateReference;
      if (templateId) {
        uniqueTemplates.add(templateId);
      }
    });

    return Array.from(uniqueTemplates).map((templateId) => ({
      label: templateNames[templateId] || `Template ${templateId}`,
      value: templateId,
    }));
  }, [jobs, templateNames]);

  // Count the number of jobs in each status and track available statuses
  useEffect(() => {
    const counts: StatusCount = { All: jobs.length };

    // Initialize counts for statuses we always want to show
    alwaysShowStatuses.forEach((status) => {
      if (status !== "All") {
        counts[status] = 0;
      }
    });

    // Count jobs by status
    jobs.forEach((job) => {
      if (counts[job.status]) {
        counts[job.status]++;
      } else {
        counts[job.status] = 1;
      }
    });

    setStatusCounts(counts);
  }, [jobs]);

  // Generate filter options based on status configuration
  const filterOptions = useMemo(() => {
    const options = [];

    // Add the statuses we always want to show first, in the specified order
    for (const status of alwaysShowStatuses) {
      const displayText = status === "All" ? "All" : capitalize(status);
      options.push({
        label: `${displayText} ${statusCounts[status] || 0}`,
        value: status,
        icon: statusIcons[status as JobStatus],
      });
    }

    // Add any additional statuses that exist in the data and aren't already added
    Object.keys(statusCounts).forEach((status) => {
      if (!alwaysShowStatuses.includes(status as JobStatus | "All") && statusCounts[status] > 0) {
        const displayText = status === "All" ? "All" : capitalize(status);
        options.push({
          label: `${displayText} ${statusCounts[status]}`,
          value: status,
          icon: statusIcons[status as JobStatus],
        });
      }
    });

    return options;
  }, [statusCounts]);

  // Apply filters when filter or jobs change
  useEffect(() => {
    // Apply status filter
    let filteredJobs = applyFilter(jobs, statusFilter);

    // Apply template filters
    if (templateFilters.length > 0) {
      filteredJobs = filteredJobs.filter((job) => {
        const templateId = (job as any).templateReference;
        return templateId && templateFilters.includes(templateId);
      });
    }

    onFiltered(filteredJobs);
  }, [statusFilter, templateFilters, jobs, applyFilter, onFiltered]);

  // Handle filter changes
  const handleStatusFilterChange = (value: string) => {
    setStatusFilter(value);
  };

  const handleTemplateFilterChange = (values: string[]) => {
    setTemplateFilters(values);
  };

  return (
    <Card
      style={{ marginBottom: "16px", background: colorBgContainer }}
      styles={{
        body: {
          padding: "5px 10px",
        },
      }}
    >
      <Row align="middle">
        <Col span={16}>
          <Segmented onChange={handleStatusFilterChange} value={statusFilter} options={filterOptions} />
        </Col>
        <Col span={8}>
          <Flex justify="end">
            <Select
              mode="multiple"
              style={{ width: 250 }}
              value={templateFilters}
              onChange={handleTemplateFilterChange}
              options={templateOptions}
              placeholder="Filter by template"
              maxTagCount="responsive"
              showSearch
              allowClear
              optionFilterProp="label"
              filterOption={(input, option) =>
                (option?.label?.toString() || "").toLowerCase().includes(input.toLowerCase())
              }
            />
          </Flex>
        </Col>
      </Row>
    </Card>
  );
}
