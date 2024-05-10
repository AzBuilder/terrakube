# Setup Terrakube Dynamic Credentials (AWS)

## Requirements

Make sure to mount your public and private key to the API container as explained [here](https://docs.terrakube.io/user-guide/workspaces/dynamic-provider-credentials#generate-public-and-private-key)

Validate the following endpoints are working:

- https://terrakube-api.mydomain.com/.well-known/jwks
- https://terrakube-api.mydomain.com/.well-known/openid-configuration

Set terraform variables using: ***"variables.auto.tfvars"***

```terraform
terrakube_token = "TERRAKUBE_PERSONAL_ACCESS_TOKEN"
terrakube_hostname = "terrakube-api.mydomain.com"
terrakube_organization_name = "simple"
terrakube_workspace_name = "dynamic-workspace"
gcp_project_id = "my-gcp-project"
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

provider "google" {
  project     = "my-gcp-project"
  region      = "us-central1"
  zone        = "us-central1-c"
}

resource "google_storage_bucket" "auto-expire" {
  name          = "mysuperbukcetname"
  location      = "US"
  force_destroy = true

  public_access_prevention = "enforced"
}
```