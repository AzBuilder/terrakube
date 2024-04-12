#!/bin/bash

#rm .env

echo "REACT_APP_TERRAKUBE_API_URL=$REACT_CONFIG_TERRAKUBE_URL" >> .env;
echo "REACT_APP_CLIENT_ID=$REACT_CONFIG_CLIENT_ID" >> .env;
echo "REACT_APP_AUTHORITY=$REACT_CONFIG_AUTHORITY" >> .env;
echo "REACT_APP_REDIRECT_URI=$REACT_CONFIG_REDIRECT" >>.env;
echo "REACT_APP_REGISTRY_URI=$REACT_CONFIG_REGISTRY_URI" >>.env;
echo "REACT_APP_SCOPE=$REACT_CONFIG_SCOPE" >>.env
echo "REACT_APP_TERRAKUBE_VERSION=$REACT_APP_TERRAKUBE_VERSION" >>.env
