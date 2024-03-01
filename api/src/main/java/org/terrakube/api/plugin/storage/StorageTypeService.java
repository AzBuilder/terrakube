package org.terrakube.api.plugin.storage;

import java.io.InputStream;
import java.util.List;

public interface StorageTypeService {

    byte[] getStepOutput(String organizationId, String jobId, String stepId);

    byte[] getTerraformPlan(String organizationId, String workspaceId, String jobId, String stepId);

    byte[] getTerraformStateJson(String organizationId, String workspaceId, String stateFileName);

    void uploadTerraformStateJson(String organizationId, String workspaceId, String stateJson, String stateJsonHistoryId);

    byte[] getCurrentTerraformState(String organizationId, String workspaceId);

    void uploadState(String organizationId, String workspaceId, String terraformState, String historyId);

    String saveContext(int jobId, String jobContext);

    String getContext(int jobId);

    void createContentFile(String contentId, InputStream inputStream);

    byte[] getContentFile(String contentId);

    void deleteModuleStorage(String organizationName, String moduleName, String providerName);

    void deleteWorkspaceOutputData(String organizationId, List<Integer> jobList);

    void deleteWorkspaceStateData(String organizationId, String workspaceId);
}
