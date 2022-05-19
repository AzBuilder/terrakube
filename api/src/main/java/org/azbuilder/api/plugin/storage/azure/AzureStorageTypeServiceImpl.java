package org.azbuilder.api.plugin.storage.azure;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.storage.StorageTypeService;

@Slf4j
@Builder
public class AzureStorageTypeServiceImpl implements StorageTypeService {

    private static final String TERRAFORM_PLAN_FILE = "terraformLibrary.tfPlan";
    private static final String CONTAINER_NAME_STATE = "tfstate";
    private static final String CONTAINER_NAME_OUTPUT = "tfoutput";

    @NonNull
    BlobServiceClient blobServiceClient;

    @Override
    public byte[] getStepOutput(String organizationId, String jobId, String stepId) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME_OUTPUT);
        log.info("Searching: /tfoutput/{}/{}/{}.tfoutput", organizationId, jobId, stepId);
        return containerClient.getBlobClient(String.format("%s/%s/%s.tfoutput",organizationId, jobId, stepId)).downloadContent().toBytes();
    }

    @Override
    public byte[] getTerraformPlan(String organizationId, String workspaceId, String jobId, String stepId) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME_STATE);
        log.info("Searching: /tfstate/{}/{}/{}/{}/terraformLibrary.tfPlan", organizationId, workspaceId, jobId, stepId);
        return containerClient.getBlobClient(String.format("%s/%s/%s/%s/terraformLibrary.tfPlan", organizationId, workspaceId, jobId, stepId)).downloadContent().toBytes();
    }

    @Override
    public byte[] getTerraformStateJson(String organizationId, String workspaceId, String stateFileName) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME_STATE);
        log.info("Searching: /tfstate/{}/{}/state/{}.json", organizationId, workspaceId, stateFileName);
        return containerClient.getBlobClient(String.format("%s/%s/state/%s.json", organizationId, workspaceId, stateFileName)).downloadContent().toBytes();
    }
}
