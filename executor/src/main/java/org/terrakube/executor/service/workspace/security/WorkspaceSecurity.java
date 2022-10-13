package org.terrakube.executor.service.workspace.security;

import java.io.File;

public interface WorkspaceSecurity {

    void addTerraformCredentials(File workingDirectory);

    String generateAccessToken();

    String generateAccessToken(int minutes);
}
