package io.terrakube.executor.service.workspace.security;

public interface WorkspaceSecurity {

    void addTerraformCredentials();

    String generateAccessToken();

    String generateAccessToken(int minutes);
}
