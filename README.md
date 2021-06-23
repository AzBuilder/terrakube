# AzBuilder - API

Open source tool to handle remote terraform workspace in organizations and handle all the lifecycle (plan, apply, destroy).

The server defines a rest API based on [Yahoo Elide](https://elide.io/) and expose a [JSON:API](https://jsonapi.org/).

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

|Name   |Description                                       |
|:------|--------------------------------------------------|
|api    | Expose the API to manage all terraform workspaces|
|api-job| Schedule job that validate the terraform workspaces with pending executions |

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
/api/v1/organization/{{organizationId}}/module/{{moduleId}}/definition
/api/v1/organization/{{organizationId}}/module/{{moduleId}}/definition/{{versionId}}/parameter
```
### Workspace
These endpoints allow creating, updating, deleting and search terraform workspaces and define different parameters like variables, secrets and environment variables.
```
/api/v1/organization/{{organizationId}}/workspace
/api/v1/organization/{{organizationId}}/workspace/{{workspaceId}}/variable
/api/v1/organization/{{organizationId}}/workspace/{{workspaceId}}/secret
/api/v1/organization/{{organizationId}}/workspace/{{workspaceId}}/environment
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
