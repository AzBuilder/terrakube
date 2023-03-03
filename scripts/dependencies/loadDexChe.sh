#!/bin/bash

CHE_DEX=$(echo "$DEVWORKSPACE_ID"-8)
CHE_UI=$(echo "$DEVWORKSPACE_ID"-4)
DOMAIN=$(echo $CHE_DASHBOARD_URL | sed "s+https://++g")

cp scripts/template/dex/template-config-ldap.yaml scripts/setup/dex/config-ldap.yaml
jwtIssuer=$(echo "http://$CHE_DEX.$DOMAIN")
sed -i "s+TEMPLATE_GITPOD_JWT_ISSUER+$jwtIssuer+gi" scripts/setup/dex/config-ldap.yaml
uiRedirect=$(echo "http://$CHE_UI.$DOMAIN")
sed -i "s+TEMPLATE_GITPOD_REDIRECT+$uiRedirect+gi" scripts/setup/dex/config-ldap.yaml
echo "Dex Enviroment setup completed, you can close this terminal"