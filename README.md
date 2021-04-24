# azb-server (work in progress)

Open source tool to handle remote terraform workspace in organizations and handle all the lifecycle (plan, apply, destroy).

The server defines a rest API based on [Yahoo Elide](https://elide.io/) and expose a [JSON:API](https://jsonapi.org/)

## Requirements

To compile and run the tool you will need the following:

* Java 11
* Maven

## Compiling

```bash
mvn clean install
```

## Running
To run the API use the following command:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
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
This endpoint define the operation to be executed for a specific terraform workspace. 
```
/api/v1/organization/{{organizationId}}/job
```

## Postman Examples
For more detail information about how to use azb-server json:api please check the following files:

* [Postman Collection](postman/azb-server.postman_collection.json)
* [Postman Environemnts](postman/AzBuilderEnvironment.postman_environment.json)