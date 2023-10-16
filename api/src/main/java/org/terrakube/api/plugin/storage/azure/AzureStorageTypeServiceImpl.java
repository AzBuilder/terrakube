package org.terrakube.api.plugin.storage.azure;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.terrakube.api.plugin.storage.StorageTypeService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Builder
public class AzureStorageTypeServiceImpl implements StorageTypeService {

    private static final String CONTAINER_NAME_STATE = "tfstate";
    private static final String CONTAINER_NAME_OUTPUT = "tfoutput";

    private static final String CONTAINER_TERRAFORM_CONTENT = "content";
    private static final String CONTEXT_FILE = "context/%s/context.json";

    private static final String TERRAFORM_TAR_GZ = "content/%s/terraformContent.tar.gz";

    @NonNull
    BlobServiceClient blobServiceClient;

    @Override
    public byte[] getStepOutput(String organizationId, String jobId, String stepId) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME_OUTPUT);
        log.info("Searching: /tfoutput/{}/{}/{}.tfoutput", organizationId, jobId, stepId);
        return containerClient.getBlobClient(String.format("%s/%s/%s.tfoutput", organizationId, jobId, stepId)).downloadContent().toBytes();
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

    @Override
    public void uploadTerraformStateJson(String organizationId, String workspaceId, String stateJson, String stateJsonHistoryId) {
        BlobContainerClient contextContainerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME_STATE);

        String stateFileName = String.format("%s/%s/state/%s.json", organizationId, workspaceId, stateJsonHistoryId);
        log.info("New State JSON Az Storage: {}", stateFileName);
        BlobClient blobClient = contextContainerClient.getBlobClient(stateFileName);

        BinaryData binaryData = BinaryData.fromBytes(stateJson.getBytes());
        blobClient.upload(binaryData, true);
    }

    @Override
    public byte[] getCurrentTerraformState(String organizationId, String workspaceId) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME_STATE);
        log.info("Searching: /{}/{}/terraform.tfstate", organizationId, workspaceId);
        return containerClient.getBlobClient(String.format("%s/%s/terraform.tfstate", organizationId, workspaceId)).downloadContent().toBytes();
    }

    @Override
    public void uploadState(String organizationId, String workspaceId, String terraformState) {
        BlobContainerClient contextContainerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME_STATE);

        String stateFileName = String.format("%s/%s/terraform.tfstate", organizationId, workspaceId);
        log.info("New State File Az Storage: {}", stateFileName);
        BlobClient blobClient = contextContainerClient.getBlobClient(stateFileName);

        BinaryData binaryData = BinaryData.fromBytes(terraformState.getBytes());
        blobClient.upload(binaryData, true);
    }

    @Override
    public String saveContext(int jobId, String jobContext) {
        BlobContainerClient contextContainerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME_OUTPUT);

        log.info("contextContainerClient.exists {}", contextContainerClient.exists());
        if (!contextContainerClient.exists()) {
            contextContainerClient.create();
        }
        String blobName = String.format(CONTEXT_FILE, jobId);
        log.info("Context file: {}", blobName);
        BlobClient blobClient = contextContainerClient.getBlobClient(blobName);

        BinaryData binaryData = BinaryData.fromBytes(jobContext.getBytes());
        blobClient.upload(binaryData, true);
        return jobContext;
    }

    @Override
    public String getContext(int jobId) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME_OUTPUT);
        log.info("Searching: /tfoutput/context/{}/context.json", jobId);
        if (containerClient.getBlobClient(String.format(CONTEXT_FILE, jobId)).exists()) {
            return containerClient.getBlobClient(String.format(CONTEXT_FILE, jobId)).downloadContent().toString();
        } else {
            return "{}";
        }
    }

    @Override
    public void createContentFile(String contentId, InputStream inputStream) {
        BlobContainerClient contentContainerClient = blobServiceClient.getBlobContainerClient(CONTAINER_TERRAFORM_CONTENT);

        log.info("contentContainerClient.exists {}", contentContainerClient.exists());

        if (!contentContainerClient.exists()) {
            contentContainerClient.create();
        }

        String blobName = String.format(TERRAFORM_TAR_GZ, contentId);
        log.info("Content file: {}", blobName);
        BlobClient blobClient = contentContainerClient.getBlobClient(blobName);

        BinaryData binaryData = null;
        try {
            binaryData = BinaryData.fromBytes(inputStream.readAllBytes());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        blobClient.upload(binaryData, true);
    }

    @Override
    public byte[] getContentFile(String contentId) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(CONTAINER_TERRAFORM_CONTENT);
        log.info("Searching: /content/{}/terraformContent.tar.gz", contentId);
        if (containerClient.getBlobClient(String.format(TERRAFORM_TAR_GZ, contentId)).exists()) {
            return containerClient.getBlobClient(String.format(TERRAFORM_TAR_GZ, contentId)).downloadContent().toBytes();
        } else {
            return "".getBytes(StandardCharsets.UTF_8);
        }
    }

    @Override
    public void deleteModuleStorage(String organizationName, String moduleName, String providerName) {
        log.warn("Delete Module Storage not supported (Azure)");
    }
}
