#!/bin/bash

docker-compose -f scripts/setup/minio/docker-compose.yaml up -d
echo "Minio Enviroment setup completed, you can close this terminal"