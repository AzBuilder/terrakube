variable "terrakube_token" {
  type = string
}

variable "terrakube_hostname" {
  type        = string
  default     = "app.terraform.io"
  description = "The hostname Terrakube instance you'd like to use with GCP"
}

variable "terrakube_organization_name" {
  type        = string
  description = "The name of your Terrakube organization"
}

variable "terrakube_workspace_name" {
  type        = string
  default     = "my-gcp-workspace"
  description = "The name of the workspace that you'd like to create and connect to GCP"
}

variable "gcp_project_id" {
  type        = string
  description = "The ID for your GCP project"
}

variable "gcp_service_list" {
  description = "APIs required for the project"
  type        = list(string)
  default = [
    "iam.googleapis.com",
    "cloudresourcemanager.googleapis.com",
    "sts.googleapis.com",
    "iamcredentials.googleapis.com"
  ]
}