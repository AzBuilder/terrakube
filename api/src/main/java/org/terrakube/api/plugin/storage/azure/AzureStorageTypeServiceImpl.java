package org.terrakube.api.plugin.storage.azure;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.terrakube.api.plugin.storage.StorageTypeService;

import java.io.InputStream;

@Slf4j
@Builder
public class AzureStorageTypeServiceImpl implements StorageTypeService {

    private static final String CONTAINER_NAME_STATE = "tfstate";
    private static final String CONTAINER_NAME_OUTPUT = "tfoutput";
    private static final String CONTEXT_FILE = "context/%s/context.json";

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

    }

    @Override
    public byte[] getContentFile(String contentId) {
        return new byte[0];
    }
}
