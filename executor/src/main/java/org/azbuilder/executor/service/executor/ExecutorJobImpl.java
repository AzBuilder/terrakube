package org.azbuilder.executor.service.executor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.TextStringBuilder;
import org.azbuilder.executor.configuration.ExecutorFlagsProperties;
import org.azbuilder.executor.service.mode.TerraformJob;
import org.azbuilder.executor.service.scripts.ScriptEngineService;
import org.azbuilder.executor.service.workspace.SetupWorkspace;
import org.azbuilder.executor.service.shutdown.ShutdownServiceImpl;
import org.azbuilder.executor.service.status.UpdateJobStatus;
import org.azbuilder.executor.service.terraform.TerraformExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
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
        File workspaceFolder = setupWorkspace.prepareWorkspace(terraformJob);

        updateJobStatus.setRunningStatus(terraformJob);
        ExecutorJobResult terraformResult = new ExecutorJobResult();
        switch (terraformJob.getType()) {
            case "terraformPlan":
                log.info("Execute Plan for Organization {} Workspace {} ", terraformJob.getOrganizationId(), terraformJob.getWorkspaceId());
                terraformResult = terraformExecutor.plan(terraformJob, workspaceFolder);
                break;
            case "terraformApply":
                log.info("Execute Apply for Organization {} Workspace {} ", terraformJob.getOrganizationId(), terraformJob.getWorkspaceId());
                terraformResult = terraformExecutor.apply(terraformJob, workspaceFolder);
                break;
            case "terraformDestroy":
                log.info("Execute Destroy for Organization {} Workspace {} ", terraformJob.getOrganizationId(), terraformJob.getWorkspaceId());
                terraformResult = terraformExecutor.destroy(terraformJob, workspaceFolder);
                break;
            case "customScripts":
            case "approval":
                log.info("Execute Groovy Script for Organization {} Workspace {} ", terraformJob.getOrganizationId(), terraformJob.getWorkspaceId());
                TextStringBuilder scriptOutput = new TextStringBuilder();
                TextStringBuilder scriptErrorOutput = new TextStringBuilder();
                Consumer<String> output = outputScripts -> scriptOutput.appendln(outputScripts);
                executionSuccess = scriptEngineService.execute(terraformJob, terraformJob.getCommandList(), workspaceFolder, output);
                terraformResult.setOutputLog(scriptOutput.toString());
                terraformResult.setOutputErrorLog(scriptErrorOutput.toString());
                break;
            default:
                executionSuccess = false;
                terraformResult = new ExecutorJobResult();
                terraformResult.setOutputLog("Command Completed");
                terraformResult.setOutputErrorLog("Command type not defined");
                break;
        }

        updateJobStatus.setCompletedStatus(executionSuccess, terraformJob, terraformResult.getOutputLog(), terraformResult.getOutputErrorLog(), terraformResult.getPlanFile());

        try {
            FileUtils.cleanDirectory(workspaceFolder);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        if (executorFlagsProperties.isBatch())
            shutdownService.shutdownApplication();
    }
}
