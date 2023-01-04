package org.terrakube.api.plugin.storage.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.terrakube.api.plugin.storage.StorageTypeService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Builder
public class AwsStorageTypeServiceImpl implements StorageTypeService {

    private static final String TERRAFORM_PLAN_FILE = "terraformLibrary.tfPlan";
    private static final String BUCKET_LOCATION_OUTPUT = "tfoutput/%s/%s/%s.tfoutput";
    private static final String BUCKET_STATE_LOCATION = "tfstate/%s/%s/%s/%s/" + TERRAFORM_PLAN_FILE;

    private static final String BUCKET_STATE_JSON = "tfstate/%s/%s/state/%s.json";
    private static final String CONTEXT_JSON = "tfoutput/context/%s/context.json";

    private static final String S3_ERROR_LOG = "S3 Not found: {}";

    @NonNull
    private AmazonS3 s3client;

    @NonNull
    private String bucketName;

    @Override
    public byte[] getStepOutput(String organizationId, String jobId, String stepId) {
        byte[] data;
        try {
            log.info("Searching: tfoutput/{}/{}/{}.tfoutput", organizationId, jobId, stepId);
            S3Object s3object = s3client.getObject(bucketName, String.format(BUCKET_LOCATION_OUTPUT, organizationId, jobId, stepId));
            S3ObjectInputStream inputStream = s3object.getObjectContent();
            data = inputStream.getDelegateStream().readAllBytes();
        } catch (IOException e) {
            log.error(S3_ERROR_LOG,e.getMessage());
            data = new byte[0];
        }
        return data;
    }

    @Override
    public byte[] getTerraformPlan(String organizationId, String workspaceId, String jobId, String stepId) {
        byte[] data;
        try {
            log.info("Searching: tfstate/{}/{}/{}/{}/terraformLibrary.tfPlan", organizationId, workspaceId, jobId, stepId);
            S3Object s3object = s3client.getObject(bucketName, String.format(BUCKET_STATE_LOCATION, organizationId, workspaceId, jobId, stepId));
            S3ObjectInputStream inputStream = s3object.getObjectContent();
            data = inputStream.getDelegateStream().readAllBytes();
        } catch (IOException e) {
            log.error(S3_ERROR_LOG,e.getMessage());
            data = new byte[0];
        }
        return data;
    }

    @Override
    public byte[] getTerraformStateJson(String organizationId, String workspaceId, String stateFileName) {
        byte[] data;
        try {
            log.info("Searching: tfstate/{}/{}/state/{}.json", organizationId, workspaceId, stateFileName);
            S3Object s3object = s3client.getObject(bucketName, String.format(BUCKET_STATE_JSON, organizationId, workspaceId, stateFileName));
            S3ObjectInputStream inputStream = s3object.getObjectContent();
            data = inputStream.getDelegateStream().readAllBytes();
        } catch (IOException e) {
            log.error(S3_ERROR_LOG,e.getMessage());
            data = new byte[0];
        }
        return data;
    }

    @Override
    public String saveContext(int jobId, String jobContext) {
        String blobKey = String.format(CONTEXT_JSON, jobId);
        log.info("context file: {}", String.format(CONTEXT_JSON, jobId));

        byte[] bytes = StringUtils.getBytesUtf8(jobContext);
        String utf8EncodedString = StringUtils.newStringUtf8(bytes);

        s3client.putObject(bucketName, blobKey, utf8EncodedString);

        return jobContext;
    }

    @Override
    public String getContext(int jobId) {
        String data;
        try {
            log.info("Searching: /tfoutput/context/{}/context.json", jobId);
            S3Object s3object = s3client.getObject(bucketName, String.format(CONTEXT_JSON, jobId));
            S3ObjectInputStream inputStream = s3object.getObjectContent();
            data = new String(inputStream.getDelegateStream().readAllBytes(), StandardCharsets.UTF_8);;
        } catch (Exception e) {
            log.error(S3_ERROR_LOG, e.getMessage());
            data = "{}";
        }
        return data;
    }

    @Override
    public void createContentFile(String contentId, InputStream inputStream) {

    }

    @Override
    public byte[] getContentFile(String contentId) {
        return new byte[0];
    }
}
