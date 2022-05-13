package org.azbuilder.api.plugin.storage.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.storage.StorageTypeService;

import java.io.IOException;

@Slf4j
@Builder
public class AwsStorageTypeServiceImpl implements StorageTypeService {

    private static final String TERRAFORM_PLAN_FILE = "terraformLibrary.tfPlan";
    private static final String BUCKET_LOCATION_OUTPUT = "tfoutput/%s/%s/%s.tfoutput";
    private static final String BUCKET_STATE_LOCATION = "tfstate/%s/%s/%s/%s/" + TERRAFORM_PLAN_FILE;

    private static final String BUCKET_STATE_JSON = "tfstate/%s/%s/state/%s.json";

    @NonNull
    private AmazonS3 s3client;

    @NonNull
    private String bucketName;

    @Override
    public byte[] getStepOutput(String organizationId, String jobId, String stepId) {
        byte data[];
        try {
            log.info("Searching: tfoutput/{}/{}/{}.tfoutput", organizationId, jobId, stepId);
            S3Object s3object = s3client.getObject(bucketName, String.format(BUCKET_LOCATION_OUTPUT, organizationId, jobId, stepId));
            S3ObjectInputStream inputStream = s3object.getObjectContent();
            data = inputStream.getDelegateStream().readAllBytes();
        } catch (IOException e) {
            log.error("Not found: {}",e.getMessage());
            data = new byte[0];
        }
        return data;
    }

    @Override
    public byte[] getTerraformPlan(String organizationId, String workspaceId, String jobId, String stepId) {
        byte data[];
        try {
            log.info("Searching: tfstate/{}/{}/{}/{}/terraformLibrary.tfPlan", organizationId, workspaceId, jobId, stepId);
            S3Object s3object = s3client.getObject(bucketName, String.format(BUCKET_STATE_LOCATION, organizationId, workspaceId, jobId, stepId));
            S3ObjectInputStream inputStream = s3object.getObjectContent();
            data = inputStream.getDelegateStream().readAllBytes();
        } catch (IOException e) {
            log.error("Not found: {}",e.getMessage());
            data = new byte[0];
        }
        return data;
    }

    @Override
    public byte[] getTerraformStateJson(String organizationId, String workspaceId, String stateFileName) {
        byte data[];
        try {
            log.info("Searching: tfstate/{}/{}/state/{}.json", organizationId, workspaceId, stateFileName);
            S3Object s3object = s3client.getObject(bucketName, String.format(BUCKET_STATE_JSON, organizationId, workspaceId, stateFileName));
            S3ObjectInputStream inputStream = s3object.getObjectContent();
            data = inputStream.getDelegateStream().readAllBytes();
        } catch (IOException e) {
            log.error("Not found: {}",e.getMessage());
            data = new byte[0];
        }
        return data;
    }
}
