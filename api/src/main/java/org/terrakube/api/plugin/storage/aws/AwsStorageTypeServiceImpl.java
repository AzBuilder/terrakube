package org.terrakube.api.plugin.storage.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.terrakube.api.plugin.storage.StorageTypeService;

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
            log.error(S3_ERROR_LOG, e.getMessage());
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
            log.error(S3_ERROR_LOG, e.getMessage());
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
            log.error(S3_ERROR_LOG, e.getMessage());
            data = new byte[0];
        }
        return data;
    }

    @Override
    public void uploadTerraformStateJson(String organizationId, String workspaceId, String stateJson, String stateJsonHistoryId) {
        String blobKey = String.format("tfstate/%s/%s/state/%s.json", organizationId, workspaceId, stateJsonHistoryId);
        log.info("terraformJsonStateFile: {}", blobKey);
        s3client.putObject(bucketName, blobKey, stateJson);
    }

    @Override
    public byte[] getCurrentTerraformState(String organizationId, String workspaceId) {
        byte[] data;
        try {
            log.info("Searching: tfstate/{}/{}/terraform.tfstate", organizationId, workspaceId);
            S3Object s3object = s3client.getObject(bucketName, String.format("tfstate/%s/%s/terraform.tfstate", organizationId, workspaceId));
            S3ObjectInputStream inputStream = s3object.getObjectContent();
            data = inputStream.getDelegateStream().readAllBytes();
        } catch (IOException e) {
            log.error(S3_ERROR_LOG, e.getMessage());
            data = new byte[0];
        }
        return data;
    }

    @Override
    public void uploadState(String organizationId, String workspaceId, String terraformState) {
        String blobKey = String.format("tfstate/%s/%s/terraform.tfstate", organizationId, workspaceId);
        log.info("terraformStateFile: {}", blobKey);
        s3client.putObject(bucketName, blobKey, terraformState);

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
            data = new String(inputStream.getDelegateStream().readAllBytes(), StandardCharsets.UTF_8);
            ;
        } catch (Exception e) {
            log.error(S3_ERROR_LOG, e.getMessage());
            data = "{}";
        }
        return data;
    }

    @Override
    public void createContentFile(String contentId, InputStream inputStream) {
        String blobKey = String.format(TERRAFORM_TAR_GZ, contentId);
        log.info("context file: {}", String.format(TERRAFORM_TAR_GZ, contentId));


        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("application/gzip");
        s3client.putObject(bucketName, blobKey, inputStream, objectMetadata);

    }

    @Override
    public byte[] getContentFile(String contentId) {
        byte[] data;
        try {
            log.info("Searching: content/{}/terraformContent.tar.gz", contentId);
            S3Object s3object = s3client.getObject(bucketName, String.format(TERRAFORM_TAR_GZ, contentId));
            S3ObjectInputStream inputStream = s3object.getObjectContent();
            data = inputStream.getDelegateStream().readAllBytes();
        } catch (Exception e) {
            log.error(S3_ERROR_LOG, e.getMessage());
            data = "".getBytes(Charset.defaultCharset());
        }
        return data;
    }

    @Override
    public void deleteModuleStorage(String organizationName, String moduleName, String providerName) {
        String registryPath = String.format("registry/%s/%s/%s/", organizationName, moduleName, providerName);
        deleteFolderFromBucket(registryPath);
    }

    @Override
    public void deleteWorkspaceOutputData(String organizationId, List<Integer> jobList) {
        for (Integer jobId: jobList){
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
        ObjectListing objectList = s3client.listObjects(bucketName, prefix);
        List<S3ObjectSummary> objectSummeryList = objectList.getObjectSummaries();
        String[] keysList = new String[objectSummeryList.size()];
        int count = 0;
        for (S3ObjectSummary summary : objectSummeryList) {
            keysList[count++] = summary.getKey();
            log.warn("File {} will be deleted.",summary.getKey());
        }
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName).withKeys(keysList);
        s3client.deleteObjects(deleteObjectsRequest);
    }
}
