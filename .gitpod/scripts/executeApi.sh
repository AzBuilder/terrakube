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

apiId=$(jps | grep "ServerApplication" | sed "s/ServerApplication//gi") 

if [ -z "$apiId" ]; then echo "API not running"; else kill $apiId; fi

cd api
mvn spring-boot:run

