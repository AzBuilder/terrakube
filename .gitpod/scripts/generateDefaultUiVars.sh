#!/bin/bash

export REACT_CONFIG_TERRAKUBE_URL=$(gp url 8080) 
export REACT_CONFIG_CLIENT_ID="example-app"
export REACT_CONFIG_AUTHORITY="https://login.microsoftonline.com/89a1b398-5a8d-4060-b3e4-9d7b849527a9"
export REDIRECT=$(gp url 3000)
export REACT_CONFIG_REGISTRY_URI=$(gp url 8075)  
export REACT_CONFIG_SCOPE="api://azbuilder/Builder.Default "

rm -f .gitpod/scripts/.envUi

echo "REACT_CONFIG_TERRAKUBE_URL=$REACT_CONFIG_TERRAKUBE_URL" >> .gitpod/scripts/.envUi
echo "REACT_CONFIG_CLIENT_ID=$REACT_CONFIG_CLIENT_ID" >> .gitpod/scripts/.envUi
echo "REACT_CONFIG_AUTHORITY=$REACT_CONFIG_AUTHORITY" >> .gitpod/scripts/.envUi
echo "REACT_CONFIG_REGISTRY_URI=$REACT_CONFIG_REGISTRY_URI" >> .gitpod/scripts/.envUi
echo "REACT_CONFIG_SCOPE=$REACT_CONFIG_SCOPE" >> .gitpod/scripts/.envUi

cp .gitpod/scripts/.envUi ui/.env
