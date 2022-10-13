package org.terrakube.api.plugin.storage.gcp;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.terrakube.api.plugin.storage.StorageTypeService;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
@Builder
public class GcpStorageTypeServiceImpl implements StorageTypeService {

    private static final String TERRAFORM_PLAN_FILE = "terraformLibrary.tfPlan";
    private static final String GCP_LOCATION_OUTPUT = "tfoutput/%s/%s/%s.tfoutput";
    private static final String GCP_STATE_LOCATION = "tfstate/%s/%s/%s/%s/" + TERRAFORM_PLAN_FILE;
    private static final String GCP_STATE_JSON = "tfstate/%s/%s/state/%s.json";
    private static final String CONTEXT_JSON = "tfoutput/context/%s/context.json";

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
}
