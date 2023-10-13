package org.terrakube.executor.service.workspace.security;

import java.io.File;

public interface WorkspaceSecurity {

    void addTerraformCredentials();

    String generateAccessToken();

    String generateAccessToken(int minutes);
}
