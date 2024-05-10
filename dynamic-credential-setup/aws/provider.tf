terraform {
  required_providers {
    terrakube = {
      source = "AzBuilder/terrakube"
    }
  }
}

provider "terrakube" {
  endpoint = "https://${var.terrakube_api_hostname}"
  token    = var.terrakube_token
}

provider "aws" {

}