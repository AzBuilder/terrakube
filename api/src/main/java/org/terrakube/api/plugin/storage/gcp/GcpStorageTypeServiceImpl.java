package org.terrakube.api.plugin.storage.gcp;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.terrakube.api.plugin.storage.StorageTypeService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Builder
public class GcpStorageTypeServiceImpl implements StorageTypeService {

    private static final String TERRAFORM_PLAN_FILE = "terraformLibrary.tfPlan";
    private static final String GCP_LOCATION_OUTPUT = "tfoutput/%s/%s/%s.tfoutput";
    private static final String GCP_STATE_LOCATION = "tfstate/%s/%s/%s/%s/" + TERRAFORM_PLAN_FILE;
    private static final String GCP_STATE_JSON = "tfstate/%s/%s/state/%s.json";
    private static final String GCP_CURRENT_STATE = "tfstate/%s/%s/terraform.tfstate/default.tfstate";
    private static final String CONTEXT_JSON = "tfoutput/context/%s/context.json";

    private static final String TERRAFORM_TAR_GZ = "content/%s/terraformContent.tar.gz";

    @NonNull
    private String bucketName;
    @NonNull
    private Storage storage;

    @Override
    public byte[] getStepOutput(String organizationId, String jobId, String stepId) {
        log.info("getStepOutput {}", String.format(GCP_LOCATION_OUTPUT, organizationId, jobId, stepId));
        return storage.get(
                        BlobId.of(
                                bucketName,
                                String.format(GCP_LOCATION_OUTPUT, organizationId, jobId, stepId)))
                .getContent();
    }

    @Override
    public byte[] getTerraformPlan(String organizationId, String workspaceId, String jobId, String stepId) {
        log.info("getTerraformPlan {}", String.format(GCP_STATE_LOCATION, organizationId, workspaceId, jobId, stepId));
        return storage.get(
                        BlobId.of(
                                bucketName,
                                String.format(GCP_STATE_LOCATION, organizationId, workspaceId, jobId, stepId)))
                .getContent();
    }

    @Override
    public byte[] getTerraformStateJson(String organizationId, String workspaceId, String stateFileName) {
        log.info("getTerraformStateJson {}", String.format(GCP_STATE_JSON, organizationId, workspaceId, stateFileName));
        return storage.get(
                        BlobId.of(
                                bucketName,
                                String.format(GCP_STATE_JSON, organizationId, workspaceId, stateFileName)))
                .getContent();
    }

    @Override
    public void uploadTerraformStateJson(String organizationId, String workspaceId, String stateJson, String stateJsonHistoryId) {
        String currentStateKey = String.format(GCP_STATE_JSON, organizationId, workspaceId, stateJsonHistoryId);
        log.info("Define new Json State File: {}", currentStateKey);

        BlobId blobId = BlobId.of(bucketName, currentStateKey);
        Blob blob = storage.get(blobId);
        if (blob != null) {
            log.info("Creating new JSON state...");
            try {
                WritableByteChannel channel = blob.writer();
                channel.write(ByteBuffer.wrap(stateJson.getBytes(Charset.defaultCharset())));
                channel.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    @Override
    public byte[] getCurrentTerraformState(String organizationId, String workspaceId) {
        log.info("getTerraformStateJson {}", String.format(GCP_CURRENT_STATE, organizationId, workspaceId));
        return storage.get(
                        BlobId.of(
                                bucketName,
                                String.format(GCP_CURRENT_STATE, organizationId, workspaceId)))
                .getContent();
    }

    @Override
    public void uploadState(String organizationId, String workspaceId, String terraformState) {
        String currentStateKey = String.format(GCP_CURRENT_STATE, organizationId, workspaceId);
        log.info("Define new Current State File: {}", currentStateKey);

        BlobId blobId = BlobId.of(bucketName, currentStateKey);
        Blob blob = storage.get(blobId);
        if (blob != null) {
            log.info("State does not exists...");
            try {
                WritableByteChannel channel = blob.writer();
                channel.write(ByteBuffer.wrap(terraformState.getBytes(Charset.defaultCharset())));
                channel.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        } else {
            log.info("Updating state...");
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            storage.create(blobInfo, terraformState.getBytes(Charset.defaultCharset()));
        }
    }

    @Override
    public String saveContext(int jobId, String jobContext) {
        String blobKey = String.format(CONTEXT_JSON, jobId);
        log.info("context file: {}", blobKey);

        String utf8EncodedString = StringUtils.newStringUtf8(StringUtils.getBytesUtf8(jobContext));

        BlobId blobId = BlobId.of(bucketName, blobKey);
        Blob blob = storage.get(blobId);
        if (blob != null) {
            try {
                WritableByteChannel channel = blob.writer();
                channel.write(ByteBuffer.wrap(jobContext.getBytes(Charset.defaultCharset())));
                channel.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        } else {
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            storage.create(blobInfo, utf8EncodedString.getBytes(Charset.defaultCharset()));
        }

        return jobContext;
    }

    @Override
    public String getContext(int jobId) {
        log.info("context {}", String.format(CONTEXT_JSON, jobId));

        if (storage.get(BlobId.of(bucketName, String.format(CONTEXT_JSON, jobId))) != null)
            return new String(storage.get(
                            BlobId.of(
                                    bucketName,
                                    String.format(CONTEXT_JSON, jobId)))
                    .getContent(), StandardCharsets.UTF_8);
        else
            return "{}";
    }

    @Override
    public void createContentFile(String contentId, InputStream inputStream) {
        String blobKey = String.format(TERRAFORM_TAR_GZ, contentId);
        log.info("context file: {}", blobKey);

        BlobId blobId = BlobId.of(bucketName, blobKey);
        Blob blob = storage.get(blobId);
        if (blob != null) {
            try {
                WritableByteChannel channel = blob.writer();
                channel.write(ByteBuffer.wrap(inputStream.readAllBytes()));
                channel.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        } else {
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            try {
                storage.create(blobInfo, inputStream.readAllBytes());
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    @Override
    public byte[] getContentFile(String contentId) {
        log.info("context {}", String.format(TERRAFORM_TAR_GZ, contentId));

        if (storage.get(BlobId.of(bucketName, String.format(TERRAFORM_TAR_GZ, contentId))) != null)
            return storage.get(
                            BlobId.of(
                                    bucketName,
                                    String.format(TERRAFORM_TAR_GZ, contentId)))
                    .getContent();
        else
            return "".getBytes(Charset.defaultCharset());
    }

    @Override
    public void deleteModuleStorage(String organizationName, String moduleName, String providerName) {
        String modulePath = String.format("registry/%s/%s/%s/", organizationName, moduleName, providerName);
        deleteFolderFromBucket(modulePath);

    }

    @Override
    public void deleteWorkspaceOutputData(String organizationId, List<Integer> jobList) {
        for (Integer jobId : jobList) {
            String outputPath = String.format("tfoutput/%s/%s/", organizationId, jobId);
            deleteFolderFromBucket(outputPath);
        }
    }

    @Override
    public void deleteWorkspaceStateData(String organizationId, String workspaceId) {
        String outputPath = String.format("tfstate/%s/%s/", organizationId, workspaceId);
        deleteFolderFromBucket(outputPath);
    }

    private void deleteFolderFromBucket(String folderPath) {
        Page<Blob> blobs =
                storage.list(
                        bucketName,
                        Storage.BlobListOption.currentDirectory(),
                        Storage.BlobListOption.prefix(folderPath)
                );

        for (Blob blob : blobs.iterateAll()) {
            if (blob.getName().endsWith("/")) {
                deleteFolderFromBucket(blob.getName());
            } else {
                log.info("Deleting object: {}", blob.getName());
                storage.delete(blob.getBlobId());
            }

        }
    }
}
