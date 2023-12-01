<br/>
<div id="terrakube" align="center">
    <br />
    <img src="https://avatars.githubusercontent.com/u/80990539?s=200&v=4" alt="Terrakube Logo" width="120"/>
    <h3>Terrakube</h3>
    <p>Open source IaC Automation and Collaboration Software.</p>
</div>

<div id="badges" align="center">

[![gitpod](https://img.shields.io/badge/Gitpod-ready--to--code-blue?logo=gitpod&style=flat-square)](https://gitpod.io/#https://github.com/AzBuilder/terrakube)
[![Build](https://github.com/AzBuilder/azb-server/actions/workflows/pull_request.yml/badge.svg)](https://github.com/AzBuilder/azb-server/actions/workflows/pull_request.yml)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=AzBuilder_azb-server&metric=coverage)](https://sonarcloud.io/dashboard?id=AzBuilder_azb-server)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/AzBuilder/azb-server/blob/main/LICENSE)
[![gitbook](https://raw.githubusercontent.com/aleen42/badges/master/src/gitbook_2.svg)](https://gitpod.io/#https://github.com/AzBuilder/terrakube)
</div>



## High Level Architecture
![Architecture](https://raw.githubusercontent.com/AzBuilder/docs/master/.gitbook/assets/TerrakubeV2.png)

For more information please visit our [documentation](https://docs.terrakube.io/).

### Getting started guide

If you want to develop or test Terrakube click in the following button to open a complete running environment in just a couple of seconds.

[![gitpod](https://img.shields.io/badge/Gitpod-ready--to--code-blue?logo=gitpod&style=flat-square)](https://gitpod.io/#https://github.com/AzBuilder/terrakube)

For more information about the test environment please refer to the following [file](development.md) and to learn about the API check the following [api information](https://docs.terrakube.io/api/methods)

### Minikube

To quickly test Terrakube in Minikube please follow [this](https://docs.terrakube.io/getting-started/deployment/minikube)

### Docker-Compose

To quickly test Terrakube in docker compose please follow [this](https://docs.terrakube.io/getting-started/docker-compose)

### Security - Authentication

Terrakube uses [DEX](https://dexidp.io/docs/connectors/) to handle authentication. Dex is an identity service that uses OpenID Connect to drive authentication for other apps.
Dex acts as a portal to other identity providers through “connectors.” This lets Terrakube defer authentication to LDAP servers, SAML providers, or established identity providers like GitHub, Google, and Active Directory.

### Modules
This project contains three modules describe below:

| Name     | Description                                                      |
|:---------|------------------------------------------------------------------|
| api      | Expose the API to manage all terraform workspaces                |
| registry | Open source terraform registry compatible with the Terrakube API |
| executor | This components run terraform jobs and use Terrakube extensions  |
| ui       | React JS terrakube front end                                     |

### Version Control Services
The platform support public and private repositories for modules and workspaces in the following providers:

* GitHub.com
* Bitbucket.com
* Gitlab.com
* Azure DevOps

For private repositories you need to use one of the following methods for authentication:

* oAuth Applications (GitHub, Bitbucket, Gitlab and Azure Devops)
* SSH Keys 
  - RSA
  - ED25519

### Requirements

To compile and run the tool you will need the following:

* Java 17
* Maven
* Node

### Compiling

```bash
mvn clean install -Dspring-boot.build-image.skip=true
cd ui 
yarn install
```

### Supported Databases
To run the api you need the following environment variables:

| Name             | Description |
|:-----------------|-------------|
| SQL Azure        | Supported   |
| PostgreSQL       | Supported   |
| MySQL            | Supported   |
| MariaDB          | Pending     |
| Oracle           | Pending     |

### Supported Storage Backend.

The platform support the following storage backends.
- Minio
- Azure Storage Account
- Google Cloud Storage
- Amazon S3

### Build Docker Images

To build the docker images for the server and server job execute the following command:
```bash
mvn spring-boot:build-image
cd ui 
docker build -t terrakube-ui:latest  .
```


### Terraform Terrakube Provider

A Terraform provider is available in [this repository](https://github.com/AzBuilder/terraform-provider-terrakube) to manage Terrakube objects like modules, teams, ssh keys, etc.

Example: 
```terraform
terraform {
  required_providers {
    terrakube = {
      source = "AzBuilder/terrakube"
    }
  }
}

provider "terrakube" {
  endpoint = "http://terrakube-api.minikube.net"
  token    = "(PERSONAL ACCESS TOKEN OR TEAM TOKEN)"
}

data "terrakube_organization" "org" {
  name = "simple"
}

resource "terrakube_team" "team" {
  name             = "TERRAKUBE_SUPER_ADMIN"
  organization_id  = data.terrakube_organization.org.id
  manage_workspace = false
  manage_module    = false
  manage_provider  = true
  manage_vcs       = true
  manage_template  = true
}

resource "terrakube_module" "module1" {
  name            = "module_public_connection"
  organization_id = data.terrakube_organization.org.id
  description     = "module_public_connection"
  provider_name   = "aws"
  source          = "https://github.com/terraform-aws-modules/terraform-aws-vpc.git"
}
```

### Sponsors

Any company can become a sponsor by donating or providing any benefit to the project or the team helping improve Terrakube.

#### JetBrains

Thank you to [<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg" alt="JetBrains" width="32"> JetBrains](https://jb.gg/OpenSource)
for providing us with free licenses to their great tools.

* [<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/IntelliJ_IDEA_icon.svg" alt="IDEA" width="32"> IDEA](https://www.jetbrains.com/idea/)
* [<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/GoLand_icon.svg" alt="GoLand" width="32"> GoLand](https://www.jetbrains.com/go/)
* [<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/WebStorm_icon.svg" alt="WebStorm" width="32"> WebStorm](https://www.jetbrains.com/webstorm/)
* [<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/DataGrip_icon.svg" alt="DataGrip" width="32"> DataGrip](https://www.jetbrains.com/datagrip/)

### Gitbook

Thank you to [<img src="https://uploads-ssl.webflow.com/5c349f90a3cd4515d0564552/5c66e5b48238e30e170da3be_logo.svg" alt="Gitbook" width="32"> Gitbook](https://www.gitbook.com/)
for providing us with free [OSS Plan](https://docs.gitbook.com/account-management/plans/apply-for-the-non-profit-open-source-plan).


#### Terraform BSL License

Hashicorp confirmed that Terrakube is compatible with the new Terraform BSL License, more information can be found in the following [discussion](https://github.com/orgs/AzBuilder/discussions/467).
