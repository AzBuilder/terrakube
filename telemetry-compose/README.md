# Terrakube Docker Compose with Open Telemetry Setup

## Local DNS entries

Update the /etc/hosts file adding the following entries:

```bash
127.0.0.1 terrakube-api
127.0.0.1 terrakube-ui
127.0.0.1 terrakube-executor
127.0.0.1 terrakube-dex
127.0.0.1 terrakube-registry
```

## Running Terrakube Locally 

```bash
git clone https://github.com/AzBuilder/terrakube.git
cd terrakube/docker-compose
docker-compose up -d
```

Terrakube will be available in the following URL:

* http://terrakube-ui:3000
  * Username: admin@example.com
  * Password: admin

## Open Telemetry Setup

Terrakube componentes active Open Telemtry Agent using the following environment variables:

### API
```
OTEL_JAVAAGENT_ENABLED=true
OTEL_TRACES_EXPORTER=jaeger
OTEL_EXPORTER_JAEGER_ENDPOINT=http://jaeger-all-in-one:14250
OTEL_SERVICE_NAME=TERRAKUBE-API
```

### Registry
```
OTEL_JAVAAGENT_ENABLED=true
OTEL_TRACES_EXPORTER=jaeger
OTEL_EXPORTER_JAEGER_ENDPOINT=http://jaeger-all-in-one:14250
OTEL_SERVICE_NAME=TERRAKUBE-REGISTRY
```

### Executor
```
OTEL_JAVAAGENT_ENABLED=true
OTEL_TRACES_EXPORTER=jaeger
OTEL_EXPORTER_JAEGER_ENDPOINT=http://jaeger-all-in-one:14250
OTEL_SERVICE_NAME=TERRAKUBE-EXECUTOR
```