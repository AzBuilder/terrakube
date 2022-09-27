package org.terrakube.executor.plugin.tfstate.aws;

import com.amazonaws.HttpMethod;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.text.TextStringBuilder;
import org.terrakube.client.TerrakubeClient;
import org.terrakube.client.model.organization.workspace.history.History;
import org.terrakube.client.model.organization.workspace.history.HistoryAttributes;
import org.terrakube.client.model.organization.workspace.history.HistoryRequest;
import org.terrakube.executor.plugin.tfstate.TerraformState;
import org.terrakube.executor.plugin.tfstate.TerraformStatePathService;
import org.terrakube.executor.service.mode.TerraformJob;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Builder
@Getter
@Setter
@Slf4j
public class AwsTerraformStateImpl implements TerraformState {

    private static final String TERRAFORM_PLAN_FILE = "terraformLibrary.tfPlan";
    private static final String BACKEND_FILE_NAME = "awsBackend.hcl";
    private static final String BACKEND_AWS_CONTENT = "\n\nterraform {\n" +
            "  backend \"s3\" {}\n" +
            "}";

    @NonNull
    private AmazonS3 s3client;

    @NonNull
    private String bucketName;

    private Regions region;

    private String accessKey;

    private String secretKey;

    private String endpoint;

    @NonNull
    TerrakubeClient terrakubeClient;

    @NonNull
    TerraformStatePathService terraformStatePathService;

    @Override
    public String getBackendStateFile(String organizationId, String workspaceId, File workingDirectory) {
        String awsBackend = BACKEND_FILE_NAME;
        try {
            TextStringBuilder awsBackendHcl = new TextStringBuilder();
            awsBackendHcl.appendln("bucket     = \"" + bucketName + "\"");
            awsBackendHcl.appendln("region     = \"" + region.getName() + "\"");
            awsBackendHcl.appendln("key        = \"tfstate/" + organizationId + "/" + workspaceId + "/terraform.tfstate" + "\"");
            awsBackendHcl.appendln("access_key = \"" + accessKey + "\"");
            awsBackendHcl.appendln("secret_key = \"" + secretKey + "\"");

            if(endpoint != null){
                awsBackendHcl.appendln("endpoint  = \"" + endpoint + "\"");
                awsBackendHcl.appendln("skip_credentials_validation  = true");
                awsBackendHcl.appendln("skip_metadata_api_check      = true");
                awsBackendHcl.appendln("skip_region_validation       = true");
                awsBackendHcl.appendln("force_path_style             = true");
            }

            File awsBackendFile = new File(
                    FilenameUtils.separatorsToSystem(
                            workingDirectory.getAbsolutePath().concat("/").concat(BACKEND_FILE_NAME)
                    )
            );
            FileUtils.writeStringToFile(awsBackendFile, awsBackendHcl.toString(), Charset.defaultCharset());

            File azureBackendMainTf = new File(
                    FilenameUtils.separatorsToSystem(
                            workingDirectory.getAbsolutePath().concat("/awsBackend.tf")
                    )
            );

            FileUtils.writeStringToFile(azureBackendMainTf, BACKEND_AWS_CONTENT, Charset.defaultCharset(), true);

        } catch (IOException e) {
            log.error(e.getMessage());
            awsBackend = null;
        }
        return awsBackend;
    }

    @Override
    public String saveTerraformPlan(String organizationId, String workspaceId, String jobId, String stepId, File workingDirectory) {

        String blobKey = "tfstate/" + organizationId + "/" + workspaceId + "/" + jobId + "/" + stepId + "/" + TERRAFORM_PLAN_FILE;
        log.info("terraformStateFile: {}", blobKey);

        File tfPlanContent = new File(workingDirectory.getAbsolutePath() + "/" + TERRAFORM_PLAN_FILE);
        log.info("terraformStateFile Path: {} {}", workingDirectory.getAbsolutePath() + "/" + TERRAFORM_PLAN_FILE, tfPlanContent.exists());
        if (tfPlanContent.exists()) {
            s3client.putObject(
                    bucketName,
                    blobKey,
                    tfPlanContent
            );

            return s3client.getUrl(bucketName, blobKey).toExternalForm();
        } else {
            return null;
        }
    }

    @Override
    public boolean downloadTerraformPlan(String organizationId, String workspaceId, String jobId, String stepId, File workingDirectory) {
        AtomicBoolean planExists = new AtomicBoolean(false);
        Optional.ofNullable(terrakubeClient.getJobById(organizationId, jobId).getData().getAttributes().getTerraformPlan())
                .ifPresent(stateUrl -> {
                    try {
                        log.info("Downloading state from {}", stateUrl);

                        log.info("Buket location: {}", new URL(stateUrl).getPath().replace(endpoint != null ? bucketName + "/tfstate" :"/tfstate","tfstate").substring(1));

                        S3Object s3object = s3client.getObject(bucketName, new URL(stateUrl).getPath().replace(endpoint != null ? bucketName + "/tfstate" :"/tfstate","tfstate").substring(1));
                        S3ObjectInputStream inputStream = s3object.getObjectContent();
                        byte[] data = inputStream.getDelegateStream().readAllBytes();

                        FileUtils.copyToFile(
                                new ByteArrayInputStream(data),
                                new File(workingDirectory.getAbsolutePath() + "/" + TERRAFORM_PLAN_FILE));
                        planExists.set(true);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                });
        return planExists.get();
    }

    @Override
    public void saveStateJson(TerraformJob terraformJob, String applyJSON) {
        if (applyJSON != null) {
            String stateFilename = UUID.randomUUID().toString();
            String blobKey = "tfstate/" + terraformJob.getOrganizationId() + "/" + terraformJob.getWorkspaceId() + "/state/" + stateFilename + ".json";
            log.info("terraformStateFile: {}", blobKey);

            byte[] bytes = StringUtils.getBytesUtf8(applyJSON);
            String utf8EncodedString = StringUtils.newStringUtf8(bytes);

            s3client.putObject(
                    bucketName,
                    blobKey,
                    utf8EncodedString
            );

            String stateURL = terraformStatePathService.getStateJsonPath(terraformJob.getOrganizationId(), terraformJob.getWorkspaceId(), stateFilename);

            HistoryRequest historyRequest = new HistoryRequest();
            History newHistory = new History();
            newHistory.setType("history");
            HistoryAttributes historyAttributes = new HistoryAttributes();
            historyAttributes.setOutput(stateURL);
            historyAttributes.setJobReference(terraformJob.getJobId());
            newHistory.setAttributes(historyAttributes);
            historyRequest.setData(newHistory);

            terrakubeClient.createHistory(historyRequest, terraformJob.getOrganizationId(), terraformJob.getWorkspaceId());
        }
    }

}