package org.terrakube.api.plugin.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageTypeService {

    byte[] getStepOutput(String organizationId, String jobId, String stepId);

    byte[] getTerraformPlan(String organizationId, String workspaceId, String jobId, String stepId);

    byte[] getTerraformStateJson(String organizationId, String workspaceId, String stateFileName);

    String saveContext(int jobId, String jobContext);

    String getContext(int jobId);

    void createContentFile(String contentId, MultipartFile multipartFile);
}
