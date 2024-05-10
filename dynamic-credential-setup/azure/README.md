# Setup Terrakube Dynamic Credentials (Azure)

## Requirements

Make sure to mount your public and private key to the API container as explained [here](https://docs.terrakube.io/user-guide/workspaces/dynamic-provider-credentials#generate-public-and-private-key)

> Mare sure the private key is in ***"pkcs8"*** format

Validate the following endpoints are working:

- https://terrakube-api.mydomain.com/.well-known/jwks
- https://terrakube-api.mydomain.com/.well-known/openid-configuration

Set terraform variables using: ***"variables.auto.tfvars"***

```terraform
terrakube_token                          = "TERRAKUBE_PERSONAL_ACCESS_TOKEN"
terrakube_api_hostname                   = "TERRAKUBE-API.MYCLUSTER.COM"
terrakube_federated_credentials_audience = "api://AzureADTokenExchange"
terrakube_organization_name              = "simple"
terrakube_workspace_name                 = "dynamic-azure"
```

> To generate the API token check [here](https://docs.terrakube.io/user-guide/organizations/api-tokens)

Run Terraform apply to create all the federated credential setup in GCP and a sample workspace in terrakube for testing

To test the following terraform code can be used:


```terraform
terraform {

  cloud {
    organization = "terrakube_organization_name"
    hostname = "terrakube-api.mydomain.com"

    workspaces {
      name = "terrakube_workspace_name"
    }
  }
}

provider "azurerm" {
  features {}
}

 resource "azurerm_resource_group" "example" {
  name     = "randomstring-aejthtyu"
  location = "East US 2"
}
```