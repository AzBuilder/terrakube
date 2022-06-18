package org.terrakube.executor.plugin.tfstate.configuration;

public enum TerraformStateType {
    AzureTerraformStateImpl,
    AwsTerraformStateImpl,

    GcpTerraformStateImpl,
    LocalTerraformStateImpl
}
