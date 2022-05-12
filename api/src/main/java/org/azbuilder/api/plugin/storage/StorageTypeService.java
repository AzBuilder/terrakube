package org.azbuilder.api.plugin.storage;

public interface StorageTypeService {

    byte[] getStepOutput(String organizationId, String jobId, String stepId);

    byte[] getTerraformPlan(String organizationId, String workspaceId, String jobId, String stepId);

    byte[] getTerraformStateJson(String organizationId, String workspaceId, String stateFileName);
}
