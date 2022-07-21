#!/bin/bash

cp .gitpod/template/dex/template-config-ldap.yaml .gitpod/setup/dex/config-ldap.yaml
jwtIssuer=$(gp url 5556)
sed -i "s+TEMPLATE_GITPOD_JWT_ISSUER+$jwtIssuer+gi" .gitpod/setup/dex/config-ldap.yaml
uiRedirect=$(gp url 3000)
sed -i "s+TEMPLATE_GITPOD_REDIRECT+$uiRedirect+gi" .gitpod/setup/dex/config-ldap.yaml
docker-compose -f .gitpod/setup/dex/docker-compose.yaml up -d
clear
echo "Dex Enviroment setup completed, you can close this terminal"