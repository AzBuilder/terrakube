package org.terrakube.executor.plugin.tfstate.local;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
import org.terrakube.client.TerrakubeClient;
import org.terrakube.client.model.organization.workspace.history.History;
import org.terrakube.client.model.organization.workspace.history.HistoryAttributes;
import org.terrakube.client.model.organization.workspace.history.HistoryRequest;
import org.terrakube.executor.plugin.tfstate.TerraformState;
import org.terrakube.executor.service.mode.TerraformJob;

@Slf4j
@Builder
@Getter
@Setter
public class LocalTerraformStateImpl implements TerraformState {

    private static final String TERRAFORM_PLAN_FILE = "terraformLibrary.tfPlan";
    private static final String LOCAL_BACKEND_DIRECTORY = "/.terraform-spring-boot/local/backend/";
    private static final String LOCAL_STATE_DIRECTORY = "/.terraform-spring-boot/local/state/";
    private static final String BACKEND_FILE_NAME = "localBackend.hcl";
    private static final String BACKEND_LOCAL_CONTENT = "\n\nterraform {\n" +
            "  backend \"local\" { }\n" +
            "}";

    @NonNull
    TerrakubeClient terrakubeClient;

    @Override
    public String getBackendStateFile(String organizationId, String workspaceId, File workingDirectory) {
        String localBackend = BACKEND_FILE_NAME;
        try {
            String localBackendDirectory = FileUtils.getUserDirectoryPath().concat(
                    FilenameUtils.separatorsToSystem(
                            LOCAL_BACKEND_DIRECTORY + String.join(File.separator, Stream.of(organizationId, workspaceId).toArray(String[]::new))
                    ));

            TextStringBuilder localBackendHcl = new TextStringBuilder();
            localBackendHcl.appendln("path                  = \"" + localBackendDirectory + "/terraform.tfstate" + "\"");

            File localBackendFile = new File(
                    FilenameUtils.separatorsToSystem(
                            String.join(File.separator, Stream.of(workingDirectory.getAbsolutePath(), BACKEND_FILE_NAME).toArray(String[]::new))
                    )
            );

            log.info("Creating Local Backend File: {}", localBackendFile.getAbsolutePath());
            FileUtils.writeStringToFile(localBackendFile, localBackendHcl.toString(), Charset.defaultCharset());

            File localBackendMainTf = new File(
                    FilenameUtils.separatorsToSystem(
                            workingDirectory.getAbsolutePath().concat("/main.tf")
                    )
            );

            FileUtils.writeStringToFile(localBackendMainTf, BACKEND_LOCAL_CONTENT, Charset.defaultCharset(), true);

        } catch (IOException e) {
            log.error(e.getMessage());
            localBackend = null;
        }
        return localBackend;
    }

    @Override
    public String saveTerraformPlan(String organizationId, String workspaceId, String jobId, String stepId, File workingDirectory) {

        String localStateFilePath = String.join(File.separator, Stream.of(organizationId, workspaceId, jobId, stepId, TERRAFORM_PLAN_FILE).toArray(String[]::new));

        String stepStateDirectory = FileUtils.getUserDirectoryPath().concat(
                FilenameUtils.separatorsToSystem(
                        LOCAL_STATE_DIRECTORY + localStateFilePath
                ));

        File tfPlan = new File(String.join(File.separator, Stream.of(workingDirectory.getAbsolutePath(), TERRAFORM_PLAN_FILE).toArray(String[]::new)));
        log.info("terraformStateFile Path: {} {}", workingDirectory.getAbsolutePath() + "/" + TERRAFORM_PLAN_FILE, tfPlan.exists());

        if (tfPlan.exists()) {
            try {
                FileUtils.copyFile(tfPlan, new File(stepStateDirectory));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            log.info("Local state file saved to {}", stepStateDirectory);
            return String.format("http://localhost:8090/state/%s", localStateFilePath);
        } else {
            return null;
        }
    }

    @Override
    public boolean downloadTerraformPlan(String organizationId, String workspaceId, String jobId, String stepId, File workingDirectory) {
        AtomicBoolean planExists = new AtomicBoolean(false);
        Optional.ofNullable(terrakubeClient.getJobById(organizationId, jobId).getData().getAttributes().getTerraformPlan())
                .ifPresent(stateFilePath -> {
                    try {
                        log.info("Copying state from {}:", stateFilePath);
                        FileUtils.copyURLToFile(
                                new URL(stateFilePath),
                                new File(
                                        String.join(
                                                File.separator, Stream.of(workingDirectory.getAbsolutePath(), TERRAFORM_PLAN_FILE).toArray(String[]::new)
                                        )
                                )
                        );
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
            String stateFileName = String.join(File.separator, Stream.of(terraformJob.getOrganizationId(), terraformJob.getWorkspaceId(), "state", UUID.randomUUID() + ".json").toArray(String[]::new));
            log.info("terraformStateFile: {}", stateFileName);

            File localStateFile = new File(FileUtils.getUserDirectoryPath()
                    .concat(
                            FilenameUtils.separatorsToSystem(
                                    LOCAL_STATE_DIRECTORY + stateFileName
                            )
                    )
            );

            try {
                FileUtils.writeStringToFile(localStateFile, applyJSON, Charset.defaultCharset());

                String stateURL = String.format("http://localhost:8080/state/%s", stateFileName);

                HistoryRequest historyRequest = new HistoryRequest();
                History newHistory = new History();
                newHistory.setType("history");
                HistoryAttributes historyAttributes = new HistoryAttributes();
                historyAttributes.setOutput(stateURL);
                newHistory.setAttributes(historyAttributes);
                historyRequest.setData(newHistory);

                terrakubeClient.createHistory(historyRequest, terraformJob.getOrganizationId(), terraformJob.getWorkspaceId());
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }
}
