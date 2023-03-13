# Terrakube Docker Compose

## Local DNS entries

Update the /etc/hosts file adding the following entries:

```bash
127.0.0.1 terrakube-api
127.0.0.1 terrakube-ui
127.0.0.1 terrakube-executor
127.0.0.1 terrakube-dex
127.0.0.1 terrakube-registry
```

## Running Terrakube Locally.

```bash
git clone https://github.com/AzBuilder/terrakube.git
cd docker-compose
docker-compose up -d
```

Terrakube will be available in the following URL:

* http://terrakube-ui:3000
  * Username: admin@example.com
  * Password: admin 