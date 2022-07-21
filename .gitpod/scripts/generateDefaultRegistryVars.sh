#!/bin/bash

export AzBuilderRegistry=$(gp url 8075)
export AzBuilderApiUrl=$(gp url 8080)
export AuthenticationValidationTypeRegistry=DEX
export TerrakubeEnableSecurity=true
export DexIssuerUri="$(gp url 5556)/dex"
export TerrakubeUiURL=$(gp url 3000)
export PatSecret=ejZRSFgheUBOZXAyUURUITUzdmdINDNeUGpSWHlDM1g=
export InternalSecret=S2JeOGNNZXJQTlpWNmhTITkha2NEKkt1VVBVQmFeQjM=
export RegistryStorageType=Local
export AppClientId=example-app
export AppIssuerUri="$(gp url 5556)/dex"
export JAVA_TOOL_OPTIONS="-Xmx256m -Xms128m"

rm -f .gitpod/scripts/.envRegistry

echo "AzBuilderRegistry=$AzBuilderRegistry" >> .gitpod/scripts/.envRegistry
echo "AzBuilderApiUrl=$AzBuilderApiUrl" >> .gitpod/scripts/.envRegistry
echo "AuthenticationValidationTypeRegistry=$AuthenticationValidationTypeRegistry" >> .gitpod/scripts/.envRegistry
echo "TerrakubeEnableSecurity=$TerrakubeEnableSecurity" >> .gitpod/scripts/.envRegistry
echo "DexIssuerUri=$DexIssuerUri" >> .gitpod/scripts/.envRegistry
echo "TerrakubeUiURL=$TerrakubeUiURL" >> .gitpod/scripts/.envRegistry
echo "PatSecret=$PatSecret" >> .gitpod/scripts/.envRegistry
echo "InternalSecret=$InternalSecret" >> .gitpod/scripts/.envRegistry
echo "RegistryStorageType=$RegistryStorageType" >> .gitpod/scripts/.envRegistry
echo "AppClientId=$AppClientId" >> .gitpod/scripts/.envRegistry
echo "AppIssuerUri=$AppIssuerUri" >> .gitpod/scripts/.envRegistry
echo "JAVA_TOOL_OPTIONS=$JAVA_TOOL_OPTIONS" >> .gitpod/scripts/.envRegistry

