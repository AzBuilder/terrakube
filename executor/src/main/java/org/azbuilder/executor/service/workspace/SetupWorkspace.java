package org.azbuilder.executor.service.workspace;

import org.azbuilder.executor.service.mode.TerraformJob;

import java.io.File;

public interface SetupWorkspace {

    File prepareWorkspace(TerraformJob terraformJob);
}
