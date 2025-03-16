// Shated
type AuditFieldBase = {
  createdDate: string;
  createdBy: string;
  updatDate?: string;
  updateBy?: string;
};

// Organization
export type Organization = {
  id: string;
  attributes: OrganizationAttributes;
};

export type OrganizationAttributes = {
  description?: string;
  name: string;
};

export type ApiResponse<T> = {
  data: T;
};

export type FlatOrganization = {
  id: string;
} & OrganizationAttributes;

// Templates

export type Template = {
  id: string;
  attributes: TemplateAttributes;
};
export type TemplateAttributes = {
  name: string;
};

// Jobs
export type Job = {
  id: string;
  attributes: JobAttributes;
};

export enum JobStatus {
  Pending = "pending",
  WaitingApproval = "waitingApproval",
  Approved = "approved",
  Queue = "queue",
  Running = "running",
  Completed = "completed",
  NoChanges = "noChanges",
  NotExecuted = "notExecuted",
  Rejected = "rejected",
  Cancelled = "cancelled",
  Failed = "failed",
  Unknown = "unknown",
}
export enum JobVia {
  Ui = "UI",
  Cli = "CLI",
  Github = "Github",
  Gitlab = "Gitlab",
  Bitbucket = "Bitbucket",
}

export type JobAttributes = {
  status: JobStatus;
  via: JobVia;
  output: string;
  approvalTeam: string;
} & AuditFieldBase;

export type JobStep = {
  id: string;
  name: string;
  stepNumber: number;
  status: JobStatus;
  output: string;
  outputLog: string;
};
