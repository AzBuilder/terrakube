# Terrakube Platform

[![Gitter](https://badges.gitter.im/AzBuilder/community.svg)](https://gitter.im/AzBuilder/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
[![Build](https://github.com/AzBuilder/azb-server/actions/workflows/pull_request.yml/badge.svg)](https://github.com/AzBuilder/azb-server/actions/workflows/pull_request.yml)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=AzBuilder_azb-server&metric=coverage)](https://sonarcloud.io/dashboard?id=AzBuilder_azb-server)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/AzBuilder/azb-server/blob/main/LICENSE)
[![gitbook](https://raw.githubusercontent.com/aleen42/badges/master/src/gitbook_2.svg)](https://azbuilder.gitbook.io/azb-builder/)

Open source Terraform Automation and Collaboration Software.

The server defines a rest API based on [Yahoo Elide](https://elide.io/) and expose a [JSON:API](https://jsonapi.org/) or [GraphQL](https://graphql.org/).

## High Level Architecture

![Architecture](https://raw.githubusercontent.com/AzBuilder/docs/master/.gitbook/assets/TerrakubeV2.png)

Component descriptions:
* **Terrakube API**:
Expose a JSON:API or GraphQL API providing endpoints to handle:
  - Organizations
  - Workspaces
  - Jobs
  - Modules
  - Providers
  - Variables
  - Global Variables
  - Workspace Schedules
  - State History
  - Personal Access Token
* **Terrakube Executor**:
  - Service that executes the terraform operations, updates the status using the Terrakube API and save the results using different cloud storage providers.
* **Terrakube Registry**:
  - Open Source terraform registry with support for the module and provider protocol.
* **Cloud Storage**:
  - Cloud storage to save terraform state and terraform outputs.
* **RDBMS**:
  - The platform can be used with any database supported by the Liquibase project.
* **Security**:
  - To handle authentication the platform uses Azure Active Directory.
* **Terrakube CLI**:
  - Go based CLI that can communicate with the Terrakube API and execute operation for organizations, workspaces, jobs, modules or providers
* **Terrakube UI**:
  - React based frontend to handle all Terrakube Operations.

For more information please visit our [documentation](https://docs.terrakube.org/).

## Getting started guide.

Please refere to the following [document](https://docs.terrakube.org/api/getting-started) to understand how Terrakube API works.

## Version Control Services
The platform support public and private repositories in the following providers:

* GitHub.com
* Bitbucket.com
* Gitlab.com
* Azure DevOps

## Requirements

To compile and run the tool you will need the following:

* Java 11
* Maven

## Compiling

```bash
mvn clean install
```

## Modules
This project contains three modules describe below:

| Name     | Description                                                      |
|:---------|------------------------------------------------------------------|
| api      | Expose the API to manage all terraform workspaces                |
| registry | Open source terraform registry compatible with the terrakube API |
| executor | This components run terraform jobs and use Terrakube extensions  |
| ui       | React JS terrakube front end                                     |

## Security - Authentication

Terrakube uses [DEX](https://dexidp.io/docs/connectors/) to handle authentication. Dex is an identity service that uses OpenID Connect to drive authentication for other apps.
Dex acts as a portal to other identity providers through “connectors.” This lets Terrakube defer authentication to LDAP servers, SAML providers, or established identity providers like GitHub, Google, and Active Directory.

## Supported Databases
To run the api you need the following environment variables:

| Name             | Description |
|:-----------------|-------------|
| SQL Azure        | Supported   |
| PostgreSQL       | Supported   |
| MySQL            | Supported   |
| MariaDB          | Pending     |
| Oracle           | Pending     |

## Running Sample Application
Please check the docker compose repository:

```bash
git clone https://github.com/AzBuilder/terrakube-docker-compose.git
cd local
docker-compose up
```

## Build Docker Images

To build the docker images for the server and server job execute the following command:
```bash
mvn spring-boot:build-image
```

## API Operations
The server supports the following endpoints:

* Organization
* Teams
* Modules
* Providers
* Templates
* Workspace
* Jobs
* Schedule
* History

For more information please refer to the [API documentation](https://docs.terrakube.org/api/methods)

## Postman Examples
For more detail information about how to use azb-server json:api please check the following files:

* [Postman Collection](postman/azb-server.postman_collection.json)
* [Postman Environments](postman/AzBuilderEnvironment.postman_environment.json)

### Sponsors

Any company can become a sponsor by donating or providing any benefit to the project or the team helping improve Terrakube.

#### JetBrains

Thank you to [<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg" alt="JetBrains" width="32"> JetBrains](https://jb.gg/OpenSource)
for providing us with free licenses to their great tools.

* [<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/IntelliJ_IDEA_icon.svg" alt="IDEA" width="32"> IDEA](https://www.jetbrains.com/idea/)
* [<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/GoLand_icon.svg" alt="GoLand" width="32"> GoLand](https://www.jetbrains.com/go/)
* [<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/WebStorm_icon.svg" alt="WebStorm" width="32"> WebStorm](https://www.jetbrains.com/webstorm/)
* [<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/DataGrip_icon.svg" alt="DataGrip" width="32"> DataGrip](https://www.jetbrains.com/datagrip/)