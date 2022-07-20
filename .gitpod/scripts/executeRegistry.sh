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

cd registry
mvn spring-boot:run

