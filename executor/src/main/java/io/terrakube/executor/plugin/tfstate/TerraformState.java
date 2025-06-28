package io.terrakube.executor.plugin.tfstate;

import io.terrakube.executor.service.mode.TerraformJob;

import java.io.File;

public interface TerraformState {

    String getBackendStateFile(String organizationId, String workspaceId, File workingDirectory, String terraformVersion);

    String saveTerraformPlan(String organizationId, String workspaceId, String jobId, String stepId, File workingDirectory);

    boolean downloadTerraformPlan(String organizationId, String workspaceId, String jobId, String stepId, File workingDirectory);

    void saveStateJson(TerraformJob terraformJob, String applyJSON, String rawState);

    String saveOutput(String organizationId, String jobId, String stepId, String output, String outputError);
}
