package org.terrakube.executor.service.workspace;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.terrakube.executor.service.mode.TerraformJob;

import java.io.File;

public interface SetupWorkspace {

    File prepareWorkspace(TerraformJob terraformJob);
}
