#!/bin/bash

export ApiDataSourceType="H2"
export GroupValidationType="DEX"
export UserValidationType="DEX"
export AuthenticationValidationType="DEX"
export TerrakubeHostname=$(gp url 8080 | sed "s+https://++g")
export AzBuilderExecutorUrl="$(gp url 5556)/api/v1/terraform-rs"
export PatSecret=ejZRSFgheUBOZXAyUURUITUzdmdINDNeUGpSWHlDM1g=
export InternalSecret=S2JeOGNNZXJQTlpWNmhTITkha2NEKkt1VVBVQmFeQjM=
export DexIssuerUri="$(gp url 5556)/dex"
export StorageType="LOCAL"
export TerrakubeUiURL=$(gp url 3000)
export JAVA_TOOL_OPTIONS="-Xmx512m -Xms256m"

rm -f .gitpod/scripts/.envApi

echo "ApiDataSourceType=$ApiDataSourceType" >> .gitpod/scripts/.envApi
echo "GroupValidationType=$GroupValidationType" >> .gitpod/scripts/.envApi
echo "UserValidationType=$UserValidationType" >> .gitpod/scripts/.envApi
echo "AuthenticationValidationType=$AuthenticationValidationType" >> .gitpod/scripts/.envApi
echo "TerrakubeHostname=$TerrakubeHostname" >> .gitpod/scripts/.envApi
echo "AzBuilderExecutorUrl=$AzBuilderExecutorUrl" >> .gitpod/scripts/.envApi
echo "PatSecret=$PatSecret" >> .gitpod/scripts/.envApi
echo "InternalSecret=$InternalSecret" >> .gitpod/scripts/.envApi
echo "DexIssuerUri=$DexIssuerUri" >> .gitpod/scripts/.envApi
echo "StorageType=$StorageType" >> .gitpod/scripts/.envApi
echo "TerrakubeUiURL=$TerrakubeUiURL" >> .gitpod/scripts/.envApi
echo "spring_profiles_active=demo" >> .gitpod/scripts/.envApi

