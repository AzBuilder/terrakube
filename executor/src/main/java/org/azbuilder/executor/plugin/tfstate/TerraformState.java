package org.azbuilder.executor.plugin.tfstate;

import org.azbuilder.executor.service.mode.TerraformJob;

import java.io.File;

public interface TerraformState {

    String getBackendStateFile(String organizationId, String workspaceId, File workingDirectory);

    String saveTerraformPlan(String organizationId, String workspaceId, String jobId, String stepId, File workingDirectory);

    boolean downloadTerraformPlan(String organizationId, String workspaceId, String jobId, String stepId, File workingDirectory);

    void saveStateJson(TerraformJob terraformJob, String applyJSON);
}
