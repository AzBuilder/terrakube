// Shated
type AuditFieldBase = {
  createdDate?: string;
  createdBy?: string;
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

// VCS

export enum VcsType {
  GITHUB = "GITHUB",
  GITLAB = "GITLAB",
  BITBUCKET = "BITBUCKET",
  AZURE_DEVOPS = "AZURE_DEVOPS",
  PUBLIC = "PUBLIC",
}

export type VcsModel = {
  id: string;
  attributes: VcsAttributes;
};

export type VcsAttributes = {
  name: string;
  vcsType: VcsType;
};

// SSH Keys

export type SshKey = {
  id: string;
  attributes: SshKeyAttributes;
};
export type SshKeyAttributes = {
  name: string;
};

// Modules
export type ModuleModel = {
  id: string;
  type: string;
  attributes: ModuleAttributes;
};

export type ModuleAttributes = {
  description: string;
  downloadQuantity: number;
  name: string;
  provider: string;
  source: string;
  folder?: string;
  versions: string[];
  registryPath: string;
  tagPrefix?: string;
} & AuditFieldBase;

export type FlatModule = {
  id: string;
} & ModuleAttributes;
