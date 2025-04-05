import { ReactNode } from 'react';

export type StackListItem = {
  id: string;
  name: string;
  description?: string;
  toolType: string;
  iacEngine: string;
  repoUrl: string;
  defaultBranch: string;
  vcsId: string;
};

export type ListStacksResponse = {
  organizationId: string;
  organizationName: string;
  stacks: StackListItem[];
};

export type CreateStackForm = {
  name: string;
  description?: string;
  toolType: string;
  iacEngine: string;
  repoUrl: string;
  defaultBranch: string;
  vcsId: string;
};

export type ToolType = {
  id: string;
  name: string;
  description?: string;
  icon?: ReactNode;
  color?: string;
}; 