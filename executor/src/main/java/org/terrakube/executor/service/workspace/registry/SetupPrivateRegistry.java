package org.terrakube.executor.service.workspace.registry;

import java.io.File;

public interface SetupPrivateRegistry {

    void addCredentials(File workingDirectory);

    String generateAccessToken();
}
