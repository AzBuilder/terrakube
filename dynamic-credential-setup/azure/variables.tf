variable "terrakube_api_hostname" {
  type        = string
  default     = "terrakube-api.minikube.net"
  description = "The terrakube API hostname example: terrakube-api.minikube.net"
}

variable "terrakube_organization_name" {
  type        = string
  description = "The name of your Terrakube organization"
}

variable "terrakube_workspace_name" {
  type        = string
  default     = "my-azure-workspace"
  description = "The name of the workspace that you'd like to create and connect to AWS"
}

variable "terrakube_federated_credentials_audience" {
  type        = string
  default     = "api://AzureADTokenExchange"
  description = "Audience for the federated credentials"
}

variable "terrakube_token" {
  type = string
}