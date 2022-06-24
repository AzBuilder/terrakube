package org.terrakube.api.plugin.storage.gcp;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.terrakube.api.plugin.storage.StorageTypeService;

@Slf4j
@Builder
public class GcpStorageTypeServiceImpl implements StorageTypeService {

    private static final String TERRAFORM_PLAN_FILE = "terraformLibrary.tfPlan";
    private static final String GCP_LOCATION_OUTPUT = "tfoutput/%s/%s/%s.tfoutput";
    private static final String GCP_STATE_LOCATION  = "tfstate/%s/%s/%s/%s/" + TERRAFORM_PLAN_FILE;
    private static final String GCP_STATE_JSON      = "tfstate/%s/%s/state/%s.json";
    private static final String GCP_ERROR_LOG = "File Not found: {}";

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
}
