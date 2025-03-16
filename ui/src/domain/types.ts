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
