#!/bin/bash

cp scripts/template/minio/docker-compose.yaml scripts/setup/minio/docker-compose.yaml
minioURL=$(gp url 9000)
sed -i "s+GITPOD_MINIO_URL+$minioURL+gi" scripts/setup/minio/docker-compose.yaml
minioConsoleURL=$(gp url 9001)
sed -i "s+GITPOD_MINIO_CONSOLE_URL+$minioConsoleURL+gi" scripts/setup/minio/docker-compose.yaml

docker-compose -f scripts/setup/minio/docker-compose.yaml up -d

echo "Minio Enviroment setup completed, you can close this terminal"