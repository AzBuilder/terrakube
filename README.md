# azb-server (work in progress)

Open source tool to handle remote terraform workspace in organization and handle all the lifecycle (plan, apply, destroy).

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

