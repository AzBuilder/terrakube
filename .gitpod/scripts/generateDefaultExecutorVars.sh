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

rm -f .gitpod/scripts/.envExecutor

echo "TerrakubeEnableSecurity=$TerrakubeEnableSecurity" >> .gitpod/scripts/.envExecutor
echo "InternalSecret=$InternalSecret" >> .gitpod/scripts/.envExecutor
echo "TerraformStateType=$TerraformStateType" >> .gitpod/scripts/.envExecutor
echo "TerraformOutputType=$TerraformOutputType" >> .gitpod/scripts/.envExecutor
echo "AzBuilderApiUrl=$AzBuilderApiUrl" >> .gitpod/scripts/.envExecutor
echo "ExecutorFlagBatch=$ExecutorFlagBatch" >> .gitpod/scripts/.envExecutor
echo "ExecutorFlagDisableAcknowledge=$ExecutorFlagDisableAcknowledge" >> .gitpod/scripts/.envExecutor
echo "TerrakubeToolsRepository=$TerrakubeToolsRepository" >> .gitpod/scripts/.envExecutor
echo "TerrakubeToolsBranch=$TerrakubeToolsBranch" >> .gitpod/scripts/.envExecutor
echo "TerrakubeRegistryDomain=$TerrakubeRegistryDomain" >> .gitpod/scripts/.envExecutor
echo "TerrakubeApiUrl=$TerrakubeApiUrl" >> .gitpod/scripts/.envExecutor
echo "JAVA_TOOL_OPTIONS=$JAVA_TOOL_OPTIONS" >> .gitpod/scripts/.envExecutor

