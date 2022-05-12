package org.azbuilder.api.plugin.storage.aws;

import com.amazonaws.services.s3.AmazonS3;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.storage.StorageTypeService;

@Slf4j
@Builder
public class AwsStorageTypeServiceImpl implements StorageTypeService {

    @NonNull
    private AmazonS3 s3client;

    @NonNull
    private String bucketName;

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
