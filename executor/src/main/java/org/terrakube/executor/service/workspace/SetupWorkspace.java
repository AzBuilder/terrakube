package org.terrakube.executor.service.workspace;

import org.terrakube.executor.service.mode.TerraformJob;

import java.io.File;

public interface SetupWorkspace {

    File prepareWorkspace(TerraformJob terraformJob);
}
