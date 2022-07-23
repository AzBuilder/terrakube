#!/bin/bash

cp scripts/template/dex/template-config-ldap.yaml scripts/setup/dex/config-ldap.yaml
jwtIssuer=$(gp url 5556)
sed -i "s+TEMPLATE_GITPOD_JWT_ISSUER+$jwtIssuer+gi" scripts/setup/dex/config-ldap.yaml
uiRedirect=$(gp url 3000)
sed -i "s+TEMPLATE_GITPOD_REDIRECT+$uiRedirect+gi" scripts/setup/dex/config-ldap.yaml
docker-compose -f scripts/setup/dex/docker-compose.yaml up -d
clear
echo "Dex Enviroment setup completed, you can close this terminal"