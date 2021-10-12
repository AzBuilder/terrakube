# Terrakube Platform

[![Gitter](https://badges.gitter.im/AzBuilder/community.svg)](https://gitter.im/AzBuilder/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
[![Build](https://github.com/AzBuilder/azb-server/actions/workflows/pull_request.yml/badge.svg)](https://github.com/AzBuilder/azb-server/actions/workflows/pull_request.yml)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=AzBuilder_azb-server&metric=coverage)](https://sonarcloud.io/dashboard?id=AzBuilder_azb-server)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/AzBuilder/azb-server/blob/main/LICENSE)
[![gitbook](https://raw.githubusercontent.com/aleen42/badges/master/src/gitbook_2.svg)](https://azbuilder.gitbook.io/azb-builder/)

Open source tool to handle remote terraform workspace in organizations and handle all the lifecycle (plan, apply, destroy).

The server defines a rest API based on [Yahoo Elide](https://elide.io/) and expose a [JSON:API](https://jsonapi.org/) or [GraphQL](https://graphql.org/).

## High Level Architecture

![Architecture](https://raw.githubusercontent.com/AzBuilder/docs/master/.gitbook/assets/diagrama-sin-titulo.png)

Component descriptions:
* **Terrakube API**:
Expose a JSON:API or GraphQL API providing endpoints to handle:
  - Organizations.
  - Workspaces
  - Jobs.
  - Modules
  - Providers
* **Terrakube Shedule Job**:
  - Automatic process that check for any pending terraform operations in any workspace (plan, apply or destroy)
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
This project contains two modules describe below:

|Name        |Description                                       |
|:-----------|--------------------------------------------------|
|api         | Expose the API to manage all terraform workspaces|
|api-job     | Schedule job that validate the terraform workspaces with pending executions |
|api-registry| Open source terraform registry compatible with the terrakube API |

## Security - Authentication

By default, Azure Active Directory protects the API authentication. To better understand Azure Active directory authentication please refer to these links:

* [Azure AD Spring Boot Starter](https://docs.microsoft.com/en-us/java/api/overview/azure/spring-boot-starter-active-directory-readme?view=azure-java-stable#:~:text=The%20azure%2Dspring%2Dboot%2D,web%20applications%20and%20resource%20servers%20.)
* [Azure OAuth 2.0 Resource Server](https://github.com/Azure/azure-sdk-for-java/tree/master/sdk/spring/azure-spring-boot-samples/azure-spring-boot-sample-active-directory-resource-server)
* [Accessing a resource server](https://docs.microsoft.com/en-us/java/api/overview/azure/spring-boot-starter-active-directory-readme?view=azure-java-stable#accessing-a-resource-server)


## Require Environment Variables
To run the api you need the following environment variables:

|Name                 |Description                                                                 |
|:--------------------|----------------------------------------------------------------------------|
|ApiDataSourceType    | (Required) SQL_AZURE or H2, more databases will be supported in the future |
|AzureAdAppId         | (Required) Azure Active Directory Application Client ID                    |
|AzureAdApiIdUri      | (Required) Azure Active Directory Application ID URI                       |
|SqlAzureServer       | (Optional) Sql Azure server Example: XXXX.database.windows.net             |
|SqlAzureDatabase     | (Optional) Sql Azure database name                                         |
|SqlAzureUser         | (Optional) Sql Azure user                                                  |
|SqlAzurePassword     | (Optional) Sql Azure password                                              |

> SQL Azure uses SqlPassword authentication for more information please [visit](https://docs.microsoft.com/en-us/sql/connect/jdbc/connecting-using-azure-active-directory-authentication?view=sql-server-ver15)  

To run the api-job you need the following environment variables:

|Name                   |Description                                              |
|:----------------------|---------------------------------------------------------|
|AzureAdAppClientId     | (Required) Azure Active Directory Application Client ID |
|AzureAdAppClientSecret | (Required) Azure Active Directory Application Secret    |
|AzureAdAppTenantId     | (Required) Azure Active Directory Tenant Id             |
|AzureAdAppScope        | (Required) Azure Active Directory Application Scope     |

To run the api-registry you need the following environment variables:

|Name                   |Description                                              |
|:----------------------|---------------------------------------------------------|
|AzBuilderRegistry      | (Required) Hostname where the application is running    |
|AzBuilderApiUrl        | (Required) URL where the API component is running       |
|AzureAdAppClientId     | (Required) Azure Active Directory Application Client ID |
|AzureAdAppClientSecret | (Required) Azure Active Directory Application Secret    |
|AzureAdAppTenantId     | (Required) Azure Active Directory Tenant Id             |
|AzureAdAppScope        | (Required) Azure Active Directory Application Scope     |
|AzureAccountName       | (Required) Azure Storage Account name to store all the module artifacts |
|AzureAccountKey        | (Required) Azure Storage Key to access the storage account              |

> Terraform cli require the registry to be deployed with https in order to work.

## Running
To run the API use the following command:

```bash
mvn spring-boot:run
```

## Build Docker Images

To build the docker images for the server and server job execute the following command:
```bash
mvn spring-boot:build-image
```

To run the container execute the following:
```bash
docker run -it -p8080:8080 -e api:1.0.0;
docker run -it -p8080:8080 -e api-job:1.0.0;
```

## API Operations
The server supports the following endpoints:

### Organization
This endpoint allows creating, updating, deleting and search organizations.
```
/api/v1/organization/
``` 
### Module
These endpoints allow creating, updating, deleting and search terraform module and define several definitions with parameters.
```
/api/v1/organization/{{organizationId}}/module
```
### Workspace
These endpoints allow creating, updating, deleting and search terraform workspaces and define different parameters like variables, secrets and environment variables.
```
/api/v1/organization/{{organizationId}}/workspace
/api/v1/organization/{{organizationId}}/workspace/{{workspaceId}}/variable
```
### Job
This endpoint defines the operation to be executed for a specific terraform workspace. 
```
/api/v1/organization/{{organizationId}}/job
```

## Postman Examples
For more detail information about how to use azb-server json:api please check the following files:

* [Postman Collection](postman/azb-server.postman_collection.json)
* [Postman Environments](postman/AzBuilderEnvironment.postman_environment.json)
