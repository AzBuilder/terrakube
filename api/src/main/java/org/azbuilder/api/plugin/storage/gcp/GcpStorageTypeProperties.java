package org.azbuilder.api.plugin.storage.gcp;

import org.azbuilder.api.plugin.storage.StorageTypeService;

public class GcpStorageTypeProperties implements StorageTypeService {
    @Override
    public byte[] getStepOutput(String organizationId, String jobId, String stepId) {
        return new byte[0];
    }

    @Override
    public byte[] getTerraformPlan(String organizationId, String workspaceId, String jobId, String stepId) {
        return new byte[0];
    }

    @Override
    public byte[] getTerraformStateJson(String organizationId, String workspaceId, String stateFileName) {
        return new byte[0];
    }
}
