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
  executionMode?: string;
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
  description: string;
  tcl: string;
  image: string;
  color?: string;
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

export enum VcsTypeExtended {
  GITHUB = "GITHUB",
  GITHUB_ENTERPRISE = "GITHUB_ENTERPRISE",
  GITLAB = "GITLAB",
  GITLAB_ENTERPRISE = "GITLAB_ENTERPRISE",
  GITLAB_COMMUNITY = "GITLAB_COMMUNITY",
  BITBUCKET = "BITBUCKET",
  BITBUCKET_SERVER = "BITBUCKET_SERVER",
  AZURE_DEVOPS = "AZURE_DEVOPS",
  AZURE_DEVOPS_SERVER = "AZURE_DEVOPS_SERVER",
  PUBLIC = "PUBLIC",
}

export type VcsModel = {
  id: string;
  attributes: VcsAttributes;
};

export type VcsAttributes = {
  name: string;
  vcsType: VcsType;
  description: string;
  clientId: string;
  callback: string;
  endpoint: string;
  apiUrl: string;
  connectionType: VcsConnectionType;
  status: VcsStatus;
} & AuditFieldBase;
export enum VcsConnectionType {
  OAUTH = "OAUTH",
  STANDALONE = "STANDALONE",
}
export enum VcsStatus {
  PENDING = "PENDING",
  COMPLETED = "COMPLETED",
  ERROR = "ERROR",
}

// SSH Keys

export type SshKey = {
  id: string;
  attributes: SshKeyAttributes;
};
export type SshKeyAttributes = {
  name: string;
  description: string;
  sshType: string;
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

// Team

export type Team = {
  id: string;
  attributes: TeamAttributes;
};

export type TeamAttributes = {
  manageCollection: boolean;
  manageJob: boolean;
  manageModule: boolean;
  manageProvider: boolean;
  manageState: boolean;
  manageTemplate: boolean;
  manageVcs: boolean;
  manageWorkspace: boolean;
  name: string;
};

// Token
export type TeamToken = {
  id: string;
  days: number;
  hours: number;
  minutes: number;
  group: string;
  description: string;
  deleted: boolean;
} & AuditFieldBase;

// Variables

export type GlobalVariable = {
  id: string;
  attributes: GlobalVariableAttributes;
};
export type GlobalVariableAttributes = {
  key: string;
  value: string;
  hcl: boolean;
  category: string;
  description: string;
  sensitive: boolean;
};

// Tags
export type Tag = {
  id: string;
  attributes: TagAttributes;
};
export type TagAttributes = {
  name: string;
};
export type WorkspaceTag = {
  id: string;
  attributes: {
    tagId: string;
  } & AuditFieldBase;
  relationships: any;
  type: string;
};

// Actions
export type Action = {
  id: string;
  attributes: ActionAttributes;
};
export type ActionAttributes = {
  name: string;
  type: string;
  category: string;
  version: string;
  active: boolean;
};

// User tokens

export type UserToken = {
  id: string;
  deleted: boolean;
  days: number;
  description: string;
} & AuditFieldBase;

// Schedules
export type Schedule = {
  id: string;
  attributes: ScheduleAttributes;
};

export type ScheduleAttributes = {
  cron: string;
  description?: string;
  enabled: boolean;
  tcl?: string;
  templateReference: string;
  name: string;
} & AuditFieldBase;

export type FlatSchedule = {
  id: string;
} & ScheduleAttributes;
