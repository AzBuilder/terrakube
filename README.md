## Terrakube Platform [![gitpod](https://img.shields.io/badge/Gitpod-ready--to--code-blue?logo=gitpod&style=flat-square)](https://gitpod.io/#https://github.com/AzBuilder/terrakube)

[![Gitter](https://badges.gitter.im/AzBuilder/community.svg)](https://gitter.im/AzBuilder/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
[![Build](https://github.com/AzBuilder/azb-server/actions/workflows/pull_request.yml/badge.svg)](https://github.com/AzBuilder/azb-server/actions/workflows/pull_request.yml)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=AzBuilder_azb-server&metric=coverage)](https://sonarcloud.io/dashboard?id=AzBuilder_azb-server)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/AzBuilder/azb-server/blob/main/LICENSE)
[![gitbook](https://raw.githubusercontent.com/aleen42/badges/master/src/gitbook_2.svg)](https://gitpod.io/#https://github.com/AzBuilder/terrakube)

Open source Terraform Automation and Collaboration Software.

The server defines a rest API based on [Yahoo Elide](https://elide.io/) and expose a [JSON:API](https://jsonapi.org/) or [GraphQL](https://graphql.org/).

## High Level Architecture
![Architecture](https://raw.githubusercontent.com/AzBuilder/docs/master/.gitbook/assets/TerrakubeV2.png)

For more information please visit our [documentation](https://docs.terrakube.org/).

### Getting started guide

If you want to develop or test Terrakube click in the following button to open a complete running environment in just a couple of seconds.

[![gitpod](https://img.shields.io/badge/Gitpod-ready--to--code-blue?logo=gitpod&style=flat-square)](https://gitpod.io/#https://github.com/AzBuilder/terrakube)

For more information about the test environment please refer to the following [file](development.md) and to learn about the API check the following [api information](https://docs.terrakube.org/api/methods)

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
The platform support public and private repositories in the following providers:

* GitHub.com
* Bitbucket.com
* Gitlab.com
* Azure DevOps

### Requirements

To compile and run the tool you will need the following:

* Java 11
* Maven
* Node

### Compiling

```bash
mvn clean install
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

### Build Docker Images

To build the docker images for the server and server job execute the following command:
```bash
mvn spring-boot:build-image
cd ui 
docker build -t terrakube-ui:latest  .
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
