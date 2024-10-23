package org.terrakube.api.plugin.storage.aws;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.sshd.common.util.io.IoUtils;
import org.terrakube.api.plugin.storage.StorageTypeService;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Builder
public class AwsStorageTypeServiceImpl implements StorageTypeService {

    private static final String TERRAFORM_PLAN_FILE = "terraformLibrary.tfPlan";
    private static final String BUCKET_LOCATION_OUTPUT = "tfoutput/%s/%s/%s.tfoutput";
    private static final String BUCKET_STATE_LOCATION = "tfstate/%s/%s/%s/%s/" + TERRAFORM_PLAN_FILE;

    private static final String BUCKET_STATE_JSON = "tfstate/%s/%s/state/%s.json";
    private static final String CONTEXT_JSON = "tfoutput/context/%s/context.json";

    private static final String S3_ERROR_LOG = "S3 Not found: {}";

    private static final String TERRAFORM_TAR_GZ = "content/%s/terraformContent.tar.gz";

    @NonNull
    private S3Client s3client;

    @NonNull
    private String bucketName;

    private byte[] downloadObjectFromBucket(String bucketName, String objectKey) {
        byte[] data;
        try {
            log.info("Bucket: {} Searching: {}", bucketName, objectKey);

            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .key(objectKey)
                    .bucket(bucketName)
                    .build();
            ResponseBytes<GetObjectResponse> objectBytes = s3client.getObject(objectRequest,
                    ResponseTransformer.toBytes());
            data = objectBytes.asByteArray();
        } catch (Exception e) {
            log.debug(S3_ERROR_LOG, e.getMessage());
            data = new byte[0];
        }
        return data;
    }

    private void uploadStringToBucket(String bucketName, String blobKey, String data){
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(blobKey)
                .build();

        s3client.putObject(putObjectRequest, RequestBody.fromString(data));
        log.info("Upload Object {} completed", blobKey);
    }

    @Override
    public byte[] getStepOutput(String organizationId, String jobId, String stepId) {
        return downloadObjectFromBucket(bucketName, String.format(BUCKET_LOCATION_OUTPUT, organizationId, jobId, stepId));
    }

    @Override
    public byte[] getTerraformPlan(String organizationId, String workspaceId, String jobId, String stepId) {
        return downloadObjectFromBucket(bucketName, String.format(BUCKET_STATE_LOCATION, organizationId, workspaceId, jobId, stepId));
    }

    @Override
    public byte[] getTerraformStateJson(String organizationId, String workspaceId, String stateFileName) {
        return downloadObjectFromBucket(bucketName, String.format(BUCKET_STATE_JSON, organizationId, workspaceId, stateFileName));
    }

    @Override
    public void uploadTerraformStateJson(String organizationId, String workspaceId, String stateJson, String stateJsonHistoryId) {
        String blobKey = String.format("tfstate/%s/%s/state/%s.json", organizationId, workspaceId, stateJsonHistoryId);
        log.info("terraformJsonStateFile: {}", blobKey);
        uploadStringToBucket(bucketName, blobKey, stateJson);
    }

    @Override
    public byte[] getCurrentTerraformState(String organizationId, String workspaceId) {
        return downloadObjectFromBucket(bucketName, String.format("tfstate/%s/%s/terraform.tfstate", organizationId, workspaceId));
    }

    @Override
    public void uploadState(String organizationId, String workspaceId, String terraformState, String historyId) {
        String blobKey = String.format("tfstate/%s/%s/terraform.tfstate", organizationId, workspaceId);
        String rawBlobKey = String.format("tfstate/%s/%s/state/%s.raw.json", organizationId, workspaceId, historyId);
        log.info("terraformStateFile: {}", blobKey);
        log.info("terraformRawStateFile: {}", rawBlobKey);
        uploadStringToBucket(bucketName, blobKey, terraformState);
        uploadStringToBucket(bucketName, rawBlobKey, terraformState);
    }

    @Override
    public String saveContext(int jobId, String jobContext) {
        String blobKey = String.format(CONTEXT_JSON, jobId);
        log.info("context file to bucket: {}", String.format(CONTEXT_JSON, jobId));
        byte[] bytes = StringUtils.getBytesUtf8(jobContext);
        String utf8EncodedString = StringUtils.newStringUtf8(bytes);
        uploadStringToBucket(bucketName, blobKey, utf8EncodedString);
        return jobContext;
    }

    @Override
    public String getContext(int jobId) {
        String data;
        byte[] bytes = downloadObjectFromBucket(bucketName, String.format(CONTEXT_JSON, jobId));
        if (bytes != null && bytes.length > 0) {
            data = new String(bytes, StandardCharsets.UTF_8);
        } else {
            data = "{}";
        }
        return data;
    }

    @Override
    public void createContentFile(String contentId, InputStream inputStream) {
        String blobKey = String.format(TERRAFORM_TAR_GZ, contentId);
        log.info("context file: {}", String.format(TERRAFORM_TAR_GZ, contentId));

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(blobKey)
                    .contentType("application/gzip")
                    .build();

            s3client.putObject(putObjectRequest, RequestBody.fromBytes(IoUtils.toByteArray(inputStream)));
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    @Override
    public byte[] getContentFile(String contentId) {
        byte[] bytes = downloadObjectFromBucket(bucketName, String.format(TERRAFORM_TAR_GZ, contentId));
        if (bytes != null && bytes.length > 0) {
            return bytes;
        } else {
            return "".getBytes(Charset.defaultCharset());
        }
    }

    @Override
    public void deleteModuleStorage(String organizationName, String moduleName, String providerName) {
        String registryPath = String.format("registry/%s/%s/%s/", organizationName, moduleName, providerName);
        deleteFolderFromBucket(registryPath);
    }

    @Override
    public void deleteWorkspaceOutputData(String organizationId, List<Integer> jobList) {
        for (Integer jobId : jobList) {
            String workspaceOutputFolder = String.format("tfoutput/%s/%s/", organizationId, jobId);
            deleteFolderFromBucket(workspaceOutputFolder);
        }
    }

    @Override
    public void deleteWorkspaceStateData(String organizationId, String workspaceId) {
        String workspaceStateFolder = String.format("tfstate/%s/%s/", organizationId, workspaceId);
        deleteFolderFromBucket(workspaceStateFolder);
    }

    private void deleteFolderFromBucket(String prefix) {
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();
        ListObjectsV2Response listObjectsV2Response = s3client.listObjectsV2(listObjectsV2Request);
        List<S3Object> contents = listObjectsV2Response.contents();

        for (S3Object content : contents) {
            log.warn("Deleting: {}",content.key());
            s3client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(content.key()).build());
        }
    }
}
