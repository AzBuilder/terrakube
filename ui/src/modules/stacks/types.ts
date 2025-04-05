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