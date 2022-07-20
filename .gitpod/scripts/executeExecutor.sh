#!/bin/bash

export TerrakubeEnableSecurity=true
export InternalSecret=S2JeOGNNZXJQTlpWNmhTITkha2NEKkt1VVBVQmFeQjM=
export TerraformStateType=LocalTerraformStateImpl
export TerraformOutputType=LocalTerraformOutputImpl
export AzBuilderApiUrl=$(gp url 8080)
export ExecutorFlagBatch=false
export ExecutorFlagDisableAcknowledge=false
export TerrakubeToolsRepository=https://github.com/AzBuilder/terrakube-extensions.git
export TerrakubeToolsBranch=main
export TerrakubeRegistryDomain=$(gp url 8075)
export TerrakubeApiUrl=$(gp url 8080)
export JAVA_TOOL_OPTIONS="-Xmx512m -Xms256m"

cd executor
mvn spring-boot:run -Djvm.options=-Xmx512

