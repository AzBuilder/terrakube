# Terrakube Platform

[![Gitter](https://badges.gitter.im/AzBuilder/community.svg)](https://gitter.im/AzBuilder/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
[![Build](https://github.com/AzBuilder/azb-server/actions/workflows/pull_request.yml/badge.svg)](https://github.com/AzBuilder/azb-server/actions/workflows/pull_request.yml)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=AzBuilder_azb-server&metric=coverage)](https://sonarcloud.io/dashboard?id=AzBuilder_azb-server)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/AzBuilder/azb-server/blob/main/LICENSE)
[![gitbook](https://raw.githubusercontent.com/aleen42/badges/master/src/gitbook_2.svg)](https://azbuilder.gitbook.io/azb-builder/)

Open source tool to handle remote terraform workspace in organizations and handle all the lifecycle (plan, apply, destroy).

The server defines a rest API based on [Yahoo Elide](https://elide.io/) and expose a [JSON:API](https://jsonapi.org/) or [GraphQL](https://graphql.org/).

## High Level Architecture

![Architecture](https://github.com/AzBuilder/docs/raw/master/.gitbook/assets/terrakube.drawio.png)

Component descriptions:
* **Terrakube API**:
Expose a JSON:API or GraphQL API providing endpoints to handle:
  - Organizations.
  - Workspaces
  - Jobs.
  - Modules
  - Providers
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

## Security - Authentication

By default, Azure Active Directory protects the API authentication. To better understand Azure Active directory authentication please refer to these links:

* [Azure AD Spring Boot Starter](https://github.com/MicrosoftDocs/azure-dev-docs/blob/main/articles/java/spring-framework/configure-spring-boot-starter-java-app-with-azure-active-directory.md)
* [OAuth 2.0 authorization code flow](https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-auth-code-flow)
* [Accessing a resource server](https://github.com/Azure-Samples/azure-spring-boot-samples/tree/main/aad/spring-cloud-azure-starter-active-directory/web-client-access-resource-server/aad-resource-server)

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
