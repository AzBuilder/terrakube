package io.terrakube.executor.service.executor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.TextStringBuilder;
import io.terrakube.executor.configuration.ExecutorFlagsProperties;
import io.terrakube.executor.service.mode.TerraformJob;
import io.terrakube.executor.service.scripts.ScriptEngineService;
import io.terrakube.executor.service.workspace.SetupWorkspace;
import io.terrakube.executor.service.shutdown.ShutdownServiceImpl;
import io.terrakube.executor.service.status.UpdateJobStatus;
import io.terrakube.executor.service.terraform.TerraformExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.util.function.Consumer;

@AllArgsConstructor
@Slf4j
@Service
public class ExecutorJobImpl implements ExecutorJob {

    SetupWorkspace setupWorkspace;
    TerraformExecutor terraformExecutor;
    UpdateJobStatus updateJobStatus;
    ExecutorFlagsProperties executorFlagsProperties;
    ShutdownServiceImpl shutdownService;
    ScriptEngineService scriptEngineService;

    @Async
    @Override
    public void createJob(TerraformJob terraformJob) {
        log.info("Create Job for Organization {} Workspace {} ", terraformJob.getOrganizationId(), terraformJob.getWorkspaceId());
        boolean executionSuccess = true;
        File terraformWorkingDir = setupWorkspace.prepareWorkspace(terraformJob);

        String commitId = "000000000";
        ExecutorJobResult terraformResult = new ExecutorJobResult();

        if (!terraformJob.getBranch().equals("remote-content"))
            commitId = getCommitId(terraformWorkingDir);

        updateJobStatus.setRunningStatus(terraformJob, commitId);

        switch (terraformJob.getType()) {
            case "terraformPlanDestroy":
            case "terraformPlan":
                log.info("Execute Plan for Organization {} Workspace {} ", terraformJob.getOrganizationId(), terraformJob.getWorkspaceId());
                terraformResult = terraformExecutor.plan(terraformJob, terraformWorkingDir, terraformJob.getType().equals("terraformPlanDestroy"));
                break;
            case "terraformApply":
                log.info("Execute Apply for Organization {} Workspace {} ", terraformJob.getOrganizationId(), terraformJob.getWorkspaceId());
                terraformResult = terraformExecutor.apply(terraformJob, terraformWorkingDir);
                break;
            case "terraformDestroy":
                log.info("Execute Destroy for Organization {} Workspace {} ", terraformJob.getOrganizationId(), terraformJob.getWorkspaceId());
                terraformResult = terraformExecutor.destroy(terraformJob, terraformWorkingDir);
                break;
            case "customScripts":
            case "approval":
                log.info("Execute Groovy Script for Organization {} Workspace {} ", terraformJob.getOrganizationId(), terraformJob.getWorkspaceId());
                TextStringBuilder scriptOutput = new TextStringBuilder();
                TextStringBuilder scriptErrorOutput = new TextStringBuilder();
                Consumer<String> output = outputScripts -> scriptOutput.appendln(outputScripts);
                executionSuccess = scriptEngineService.execute(terraformJob, terraformJob.getCommandList(), terraformWorkingDir, output);
                terraformResult.setOutputLog(scriptOutput.toString());
                terraformResult.setOutputErrorLog(scriptErrorOutput.toString());
                terraformResult.setSuccessfulExecution(executionSuccess);
                break;
            default:
                terraformResult = new ExecutorJobResult();
                terraformResult.setOutputLog("Command Completed");
                terraformResult.setOutputErrorLog("Command type not defined");
                terraformResult.setSuccessfulExecution(false);
                break;
        }


        executionSuccess = terraformResult.isSuccessfulExecution();
        updateJobStatus.setCompletedStatus(executionSuccess, terraformResult.isPlan, terraformResult.getExitCode(), terraformJob, terraformResult.getOutputLog(), terraformResult.getOutputErrorLog(), terraformResult.getPlanFile(), commitId);

        try {
            FileUtils.cleanDirectory(terraformWorkingDir);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        if (executorFlagsProperties.isEphemeral())
            shutdownService.shutdownApplication();
    }

    private static String getCommitId(File workspaceFolder) {
        String commitId = "";
        try {
            final File commitInformation = new File(String.format("%s/commitHash.info", workspaceFolder.getCanonicalPath()));
            final InputStream commitIdStream = new DataInputStream(new FileInputStream(commitInformation));
            commitId = IOUtils.toString(commitIdStream, Charset.defaultCharset());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return commitId;
    }
}
