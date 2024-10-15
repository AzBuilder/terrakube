terraform {
  required_providers {
    terrakube = {
      source = "AzBuilder/terrakube"
    }
  }
}

provider "google" {
  project = var.gcp_project_id
  region  = "global"
}

provider "terrakube" {
  endpoint = "https://${var.terrakube_hostname}"
  token    = var.terrakube_token
}