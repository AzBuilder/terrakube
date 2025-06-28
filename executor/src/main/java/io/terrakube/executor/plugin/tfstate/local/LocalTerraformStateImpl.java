package io.terrakube.executor.plugin.tfstate.local;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.text.TextStringBuilder;
import io.terrakube.client.TerrakubeClient;
import io.terrakube.client.model.organization.workspace.history.History;
import io.terrakube.client.model.organization.workspace.history.HistoryAttributes;
import io.terrakube.client.model.organization.workspace.history.HistoryRequest;
import io.terrakube.executor.plugin.tfstate.TerraformOutputPathService;
import io.terrakube.executor.plugin.tfstate.TerraformState;
import io.terrakube.executor.plugin.tfstate.TerraformStatePathService;
import io.terrakube.executor.service.mode.TerraformJob;

@Slf4j
@Builder
@Getter
@Setter
public class LocalTerraformStateImpl implements TerraformState {

    private static final String TERRAFORM_PLAN_FILE = "terraformLibrary.tfPlan";
    private static final String TERRAFORM_STATE_FILE = "terraform.tfstate";
    private static final String LOCAL_BACKEND_DIRECTORY = "/.terraform-spring-boot/local/backend/%s/%s/" + TERRAFORM_STATE_FILE;
    private static final String LOCAL_PLAN_DIRECTORY = "/.terraform-spring-boot/local/state/%s/%s/%s/%s/" + TERRAFORM_PLAN_FILE;
    private static final String LOCAL_PLAN_DIRECTORY_JSON = "/.terraform-spring-boot/local/state/%s/%s/state/%s.json";
    private static final String BACKEND_FILE_NAME = "terrakube_override.tf";
    private static final String LOCAL_OUTPUT_DIRECTORY = "/.terraform-spring-boot/local/output/%s/%s/%s.tfoutput";

    @NonNull
    TerraformOutputPathService terraformOutputPathService;

    @NonNull
    TerrakubeClient terrakubeClient;

    @NonNull
    TerraformStatePathService terraformStatePathService;

    @Override
    public String getBackendStateFile(String organizationId, String workspaceId, File workingDirectory, String terraformVersion) {
        log.info("Generating backend override file for terraform {}", terraformVersion);
        String localBackend = BACKEND_FILE_NAME;
        try {
            String localBackendDirectory = FileUtils.getUserDirectoryPath().concat(
                    FilenameUtils.separatorsToSystem(
                            String.format(LOCAL_BACKEND_DIRECTORY, organizationId, workspaceId)));

            TextStringBuilder localBackendHcl = new TextStringBuilder();
            localBackendHcl.appendln("terraform {");
            localBackendHcl.appendln("  backend \"local\" {");
            localBackendHcl.appendln("    path                  = \"" + localBackendDirectory + "\"");
            localBackendHcl.appendln("  }");
            localBackendHcl.appendln("}");

            File localBackendFile = new File(
                    FilenameUtils.separatorsToSystem(
                            String.join(File.separator, Stream.of(workingDirectory.getAbsolutePath(), BACKEND_FILE_NAME)
                                    .toArray(String[]::new))));

            log.info("Creating Local Backend File: {}", localBackendFile.getAbsolutePath());
            FileUtils.writeStringToFile(localBackendFile, localBackendHcl.toString(), Charset.defaultCharset());
        } catch (IOException e) {
            log.error(e.getMessage());
            localBackend = null;
        }
        return localBackend;
    }

    @Override
    public String saveTerraformPlan(String organizationId, String workspaceId, String jobId, String stepId,
                                    File workingDirectory) {

        String localStateFilePath = String.format(LOCAL_PLAN_DIRECTORY, organizationId, workspaceId, jobId, stepId);

        String stepStateDirectory = FileUtils.getUserDirectoryPath().concat(
                FilenameUtils.separatorsToSystem(
                        localStateFilePath));

        File tfPlan = new File(String.join(File.separator,
                Stream.of(workingDirectory.getAbsolutePath(), TERRAFORM_PLAN_FILE).toArray(String[]::new)));
        log.info("terraformStateFile Path: {} {}", workingDirectory.getAbsolutePath() + "/" + TERRAFORM_PLAN_FILE,
                tfPlan.exists());

        if (tfPlan.exists()) {
            try {
                FileUtils.copyFile(tfPlan, new File(stepStateDirectory));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            log.info("Local state file saved to {}", stepStateDirectory);
            return stepStateDirectory;
        } else {
            return null;
        }
    }

    @Override
    public boolean downloadTerraformPlan(String organizationId, String workspaceId, String jobId, String stepId,
                                         File workingDirectory) {
        AtomicBoolean planExists = new AtomicBoolean(false);
        Optional.ofNullable(
                        terrakubeClient.getJobById(organizationId, jobId).getData().getAttributes().getTerraformPlan())
                .ifPresent(stateFilePath -> {
                    try {
                        log.info("Copying state from {}:", stateFilePath);
                        FileUtils.copyFile(
                                new File(stateFilePath),
                                new File(
                                        String.join(
                                                File.separator,
                                                Stream.of(workingDirectory.getAbsolutePath(), TERRAFORM_PLAN_FILE)
                                                        .toArray(String[]::new))));
                        planExists.set(true);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                });
        return planExists.get();
    }

    @Override
    public void saveStateJson(TerraformJob terraformJob, String applyJSON, String rawState) {
        if (applyJSON != null) {
            String stateFilenameUUID = UUID.randomUUID().toString();
            String stateFileName = String.format(LOCAL_PLAN_DIRECTORY_JSON, terraformJob.getOrganizationId(),
                    terraformJob.getWorkspaceId(), stateFilenameUUID);
            log.info("terraformStateFile: {}", stateFileName);

            File localStateFile = new File(FileUtils.getUserDirectoryPath()
                    .concat(
                            FilenameUtils.separatorsToSystem(
                                    stateFileName)));

            File localRawStateFile = new File(FileUtils.getUserDirectoryPath()
                    .concat(
                            FilenameUtils.separatorsToSystem(
                                    stateFileName.replace(".json", ".raw.json"))));

            try {
                FileUtils.writeStringToFile(localStateFile, applyJSON, Charset.defaultCharset());
                FileUtils.writeStringToFile(localRawStateFile, rawState, Charset.defaultCharset());

                String stateURL = terraformStatePathService.getStateJsonPath(terraformJob.getOrganizationId(),
                        terraformJob.getWorkspaceId(), stateFilenameUUID);

                HistoryRequest historyRequest = new HistoryRequest();
                History newHistory = new History();
                newHistory.setType("history");
                HistoryAttributes historyAttributes = new HistoryAttributes();
                historyAttributes.setOutput(stateURL);
                historyAttributes.setSerial(1);
                historyAttributes.setMd5("0");
                historyAttributes.setLineage("0");
                historyAttributes.setJobReference(terraformJob.getJobId());
                newHistory.setAttributes(historyAttributes);
                historyRequest.setData(newHistory);

                terrakubeClient.createHistory(historyRequest, terraformJob.getOrganizationId(),
                        terraformJob.getWorkspaceId());
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    @Override
    public String saveOutput(String organizationId, String jobId, String stepId, String output, String outputError) {
        String outputFilePath = String.format(LOCAL_OUTPUT_DIRECTORY, organizationId, jobId , stepId);
        log.info("blobName: {}", outputFilePath);

        File localOutputDirectory = new File(FileUtils.getUserDirectoryPath().concat(
                FilenameUtils.separatorsToSystem(
                        outputFilePath
                )));

        log.info("Creating Output File: {}", localOutputDirectory.getAbsolutePath());
        try {
            FileUtils.writeStringToFile(localOutputDirectory, output + outputError, Charset.defaultCharset());
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return terraformOutputPathService.getOutputPath(organizationId, jobId, stepId);

    }
}
