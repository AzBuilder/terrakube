package org.terrakube.executor.service.terraform;

import com.diogonunes.jcolor.AnsiFormat;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.TextStringBuilder;
import org.terrakube.executor.plugin.tfstate.TerraformState;
import org.terrakube.executor.service.executor.ExecutorJobResult;
import org.terrakube.executor.service.mode.TerraformJob;
import org.terrakube.executor.service.scripts.ScriptEngineService;
import org.jetbrains.annotations.NotNull;
import org.terrakube.terraform.TerraformClient;
import org.springframework.stereotype.Service;
import org.terrakube.executor.service.logs.*;
import org.terrakube.terraform.TerraformProcessData;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.diogonunes.jcolor.Ansi.colorize;
import static com.diogonunes.jcolor.Attribute.*;
import static org.terrakube.executor.service.workspace.SetupWorkspaceImpl.SSH_DIRECTORY;

@AllArgsConstructor
@Slf4j
@Service
public class TerraformExecutorServiceImpl implements TerraformExecutor {

    private static final String STEP_SEPARATOR = "***************************************";

    TerraformClient terraformClient;
    TerraformState terraformState;
    ScriptEngineService scriptEngineService;

    LogsService logsService;

    @Override
    public ExecutorJobResult plan(TerraformJob terraformJob, File workingDirectory, boolean isDestroy) {
        ExecutorJobResult result;

        TextStringBuilder jobOutput = new TextStringBuilder();
        TextStringBuilder jobErrorOutput = new TextStringBuilder();
        try {
            boolean executionPlan = false;
            boolean scriptBeforeSuccessPlan;
            boolean scriptAfterSuccessPlan;

            Consumer<String> planOutput = LogsConsumer.builder()
                    .jobId(Integer.valueOf(terraformJob.getJobId()))
                    .terraformOutput(jobOutput)
                    .stepId(terraformJob.getStepId())
                    .processLogs(logsService)
                    .lineNumber(new AtomicInteger(0))
                    .build();

            Consumer<String> planOutputError = LogsConsumer.builder()
                    .jobId(Integer.valueOf(terraformJob.getJobId()))
                    .terraformOutput(jobErrorOutput)
                    .stepId(terraformJob.getStepId())
                    .lineNumber(new AtomicInteger(0))
                    .processLogs(logsService)
                    .build();

            executeTerraformInit(
                    terraformJob,
                    workingDirectory,
                    planOutput,
                    planOutputError);

            scriptBeforeSuccessPlan = executePrepOperationScripts(terraformJob, workingDirectory, planOutput);

            showTerraformMessage("PLAN", planOutput);
            if (scriptBeforeSuccessPlan)
                if (isDestroy) {
                    log.warn("Executor running a plan to destroy resources...");
                    executionPlan = terraformClient.planDestroy(
                            getTerraformProcessData(terraformJob, workingDirectory),
                            planOutput,
                            planOutputError).get();
                } else {
                    executionPlan = terraformClient.plan(
                            getTerraformProcessData(terraformJob, workingDirectory),
                            planOutput,
                            planOutputError).get();
                }

            log.warn("Terraform plan Executed Successfully: {}", executionPlan);

            scriptAfterSuccessPlan = executePostOperationScripts(terraformJob, workingDirectory, planOutput, executionPlan);


            Thread.sleep(10000);

            result = generateJobResult(scriptAfterSuccessPlan, jobOutput.toString(), jobErrorOutput.toString());
            result.setPlanFile(executionPlan ? terraformState.saveTerraformPlan(terraformJob.getOrganizationId(),
                    terraformJob.getWorkspaceId(), terraformJob.getJobId(), terraformJob.getStepId(), workingDirectory)
                    : "");
        } catch (IOException | ExecutionException | InterruptedException exception) {
            result = setError(exception);
        }
        return result;

    }

    @Override
    public ExecutorJobResult apply(TerraformJob terraformJob, File workingDirectory) {
        ExecutorJobResult result;

        TextStringBuilder terraformOutput = new TextStringBuilder();
        TextStringBuilder terraformErrorOutput = new TextStringBuilder();
        try {
            Consumer<String> applyOutput = LogsConsumer.builder()
                    .jobId(Integer.valueOf(terraformJob.getJobId()))
                    .lineNumber(new AtomicInteger(0))
                    .terraformOutput(terraformOutput)
                    .stepId(terraformJob.getStepId())
                    .processLogs(logsService)
                    .build();

            Consumer<String> applyErrorOutput = LogsConsumer.builder()
                    .jobId(Integer.valueOf(terraformJob.getJobId()))
                    .terraformOutput(terraformErrorOutput)
                    .stepId(terraformJob.getStepId())
                    .processLogs(logsService)
                    .lineNumber(new AtomicInteger(0))
                    .build();

            HashMap<String, String> terraformParameters = getWorkspaceParameters(terraformJob.getVariables());
            HashMap<String, String> environmentVariables = getWorkspaceParameters(terraformJob.getEnvironmentVariables());

            boolean execution = false;
            boolean scriptBeforeSuccess;
            boolean scriptAfterSuccess;

            executeTerraformInit(
                    terraformJob,
                    workingDirectory,
                    applyOutput,
                    applyErrorOutput);

            scriptBeforeSuccess = executePrepOperationScripts(terraformJob, workingDirectory, applyOutput);

            showTerraformMessage("APPLY", applyOutput);
            if (scriptBeforeSuccess) {
                TerraformProcessData terraformProcessData = getTerraformProcessData(terraformJob, workingDirectory);
                terraformProcessData.setTerraformVariables((terraformState.downloadTerraformPlan(terraformJob.getOrganizationId(),
                        terraformJob.getWorkspaceId(), terraformJob.getJobId(), terraformJob.getStepId(),
                        workingDirectory) ? new HashMap<>() : terraformParameters));
                execution = terraformClient.apply(
                        terraformProcessData,
                        applyOutput,
                        applyErrorOutput).get();

                handleTerraformStateChange(terraformJob, workingDirectory);

            }

            log.warn("Terraform apply Executed Successfully: {}", execution);
            scriptAfterSuccess = executePostOperationScripts(terraformJob, workingDirectory, applyOutput, execution);


            Thread.sleep(10000);
            result = generateJobResult(scriptAfterSuccess, terraformOutput.toString(), terraformErrorOutput.toString());
        } catch (IOException | ExecutionException | InterruptedException exception) {
            result = setError(exception);
        }
        return result;
    }

    @Override
    public ExecutorJobResult destroy(TerraformJob terraformJob, File workingDirectory) {
        ExecutorJobResult result;

        TextStringBuilder jobOutput = new TextStringBuilder();
        TextStringBuilder jobErrorOutput = new TextStringBuilder();
        try {

            Consumer<String> outputDestroy = LogsConsumer.builder()
                    .jobId(Integer.valueOf(terraformJob.getJobId()))
                    .terraformOutput(jobOutput)
                    .stepId(terraformJob.getStepId())
                    .processLogs(logsService)
                    .lineNumber(new AtomicInteger(0))
                    .build();

            Consumer<String> errorOutputDestroy = LogsConsumer.builder()
                    .lineNumber(new AtomicInteger(0))
                    .jobId(Integer.valueOf(terraformJob.getJobId()))
                    .terraformOutput(jobErrorOutput)
                    .stepId(terraformJob.getStepId())
                    .processLogs(logsService)
                    .build();

            boolean execution = false;
            boolean scriptBeforeSuccess;
            boolean scriptAfterSuccess;

            executeTerraformInit(
                    terraformJob,
                    workingDirectory,
                    outputDestroy,
                    errorOutputDestroy);

            scriptBeforeSuccess = executePrepOperationScripts(terraformJob, workingDirectory, outputDestroy);

            showTerraformMessage("DESTROY", outputDestroy);
            if (scriptBeforeSuccess) {
                execution = terraformClient.destroy(
                        getTerraformProcessData(terraformJob, workingDirectory),
                        outputDestroy,
                        errorOutputDestroy).get();

                handleTerraformStateChange(terraformJob, workingDirectory);
            }

            log.warn("Terraform destroy Executed Successfully: {}", execution);
            scriptAfterSuccess = executePostOperationScripts(terraformJob, workingDirectory, outputDestroy, execution);

            Thread.sleep(10000);
            result = generateJobResult(scriptAfterSuccess, jobOutput.toString(), jobErrorOutput.toString());
        } catch (IOException | ExecutionException | InterruptedException exception) {
            result = setError(exception);
        }
        return result;
    }

    private ExecutorJobResult generateJobResult(boolean scriptAfterSuccess, String jobOutput, String jobErrorOutput) {
        ExecutorJobResult jobResult = new ExecutorJobResult();
        jobResult.setSuccessfulExecution(scriptAfterSuccess);
        jobResult.setOutputLog(jobOutput);
        jobResult.setOutputErrorLog(jobErrorOutput);

        return jobResult;
    }

    private boolean executePrepOperationScripts(TerraformJob terraformJob, File workingDirectory, Consumer<String> output) {
        boolean scriptBeforeSuccess;
        if (terraformJob.getCommandList() != null)
            scriptBeforeSuccess = scriptEngineService.execute(
                    terraformJob,
                    terraformJob
                            .getCommandList()
                            .stream()
                            .filter(command -> command.isBefore())
                            .collect(Collectors.toCollection(LinkedList::new)),
                    workingDirectory,
                    output);
        else {
            log.warn("No commands to run before terraform operation Job {}", terraformJob.getJobId());
            scriptBeforeSuccess = true;
        }
        return scriptBeforeSuccess;
    }

    private boolean executePostOperationScripts(TerraformJob terraformJob, File workingDirectory, Consumer<String> output, boolean execution) {
        boolean scriptAfterSuccess;
        if (execution) {
            if (terraformJob.getCommandList() != null) {
                scriptAfterSuccess = scriptEngineService.execute(
                        terraformJob,
                        terraformJob
                                .getCommandList()
                                .stream()
                                .filter(command -> command.isAfter())
                                .collect(Collectors.toCollection(LinkedList::new)),
                        workingDirectory,
                        output);
            } else {
                scriptAfterSuccess = true;
            }
        } else {
            scriptAfterSuccess = false;
        }

        log.warn("No commands to run after terraform operation Job {}", scriptAfterSuccess);
        return scriptAfterSuccess;
    }

    private void handleTerraformStateChange(TerraformJob terraformJob, File workingDirectory)
            throws IOException, ExecutionException, InterruptedException {
        log.info("Running Terraform show");
        TextStringBuilder jsonState = new TextStringBuilder();
        Consumer<String> applyJSON = getStringConsumer(jsonState);
        Boolean showPlan = terraformClient.show(getTerraformProcessData(terraformJob, workingDirectory), applyJSON, applyJSON).get();
        if (Boolean.TRUE.equals(showPlan)) {
            log.info("Uploading terraform state json");
            terraformState.saveStateJson(terraformJob, jsonState.toString());

            TextStringBuilder jsonOutput = new TextStringBuilder();
            Consumer<String> terraformJsonOutput = getStringConsumer(jsonOutput);

            log.info("Checking terraform output json");
            Boolean showOutput = terraformClient.output(getTerraformProcessData(terraformJob, workingDirectory), terraformJsonOutput, terraformJsonOutput).get();
            if (Boolean.TRUE.equals(showOutput))
                terraformJob.setTerraformOutput(jsonOutput.toString());
        }
    }

    @Override
    public String version() {
        String terraformVersion = "";
        TextStringBuilder terraformOutput = new TextStringBuilder();
        TextStringBuilder terraformErrorOutput = new TextStringBuilder();
        try {
            terraformClient.setOutputListener(response -> {
                terraformOutput.appendln(response);
            });
            terraformClient.setErrorListener(response -> {
                terraformErrorOutput.appendln(response);
            });
            terraformVersion = terraformClient.version().get();
        } catch (IOException | ExecutionException | InterruptedException exception) {
            setError(exception);
        }
        return terraformVersion;
    }

    private ExecutorJobResult setError(Exception exception) {
        ExecutorJobResult error = generateJobResult(false, "", exception.getMessage());
        log.error(exception.getMessage());

        if (exception instanceof InterruptedException)
            Thread.currentThread().interrupt();
        return error;
    }

    private String executeTerraformInit(TerraformJob terraformJob, File workingDirectory, Consumer<String> output,
                                        Consumer<String> errorOutput) throws IOException, ExecutionException, InterruptedException {
        initBanner(terraformJob, output);
        TerraformProcessData terraformProcessData = getTerraformProcessData(terraformJob, workingDirectory);
        terraformProcessData.setTerraformEnvironmentVariables(new HashMap<>());
        terraformProcessData.setTerraformVariables(new HashMap<>());
        terraformClient.init(terraformProcessData, output, errorOutput).get();
        return terraformProcessData.getTerraformBackendConfigFileName();
    }

    private HashMap<String, String> getWorkspaceParameters(HashMap<String, String> parameters) {
        return parameters != null ? parameters : new HashMap<>();
    }

    @NotNull
    private Consumer<String> getStringConsumer(TextStringBuilder terraformOutput) {
        return responseOutput -> {
            log.info(responseOutput);
            terraformOutput.appendln(responseOutput);
        };
    }

    private void initBanner(TerraformJob terraformJob, Consumer<String> output) {
        AnsiFormat colorMessage = new AnsiFormat(GREEN_TEXT(), BLACK_BACK(), BOLD());
        output.accept(colorize(STEP_SEPARATOR, colorMessage));
        output.accept(
                colorize("Initializing Terrakube Job " + terraformJob.getJobId() + " Step " + terraformJob.getStepId(),
                        colorMessage));
        output.accept(colorize("Running Terraform " + terraformJob.getTerraformVersion(), colorMessage));
        output.accept(colorize("\n\n" + STEP_SEPARATOR, colorMessage));
        output.accept(colorize("Running Terraform Init: ", colorMessage));
    }

    private void showTerraformMessage(String operation, Consumer<String> output) {
        AnsiFormat colorMessage = new AnsiFormat(GREEN_TEXT(), BLACK_BACK(), BOLD());
        output.accept(colorize(STEP_SEPARATOR, colorMessage));
        output.accept(colorize("Running Terraform " + operation, colorMessage));
        output.accept(colorize(STEP_SEPARATOR, colorMessage));
    }

    private TerraformProcessData getTerraformProcessData(TerraformJob terraformJob, File workingDirectory) {

        terraformState.getBackendStateFile(terraformJob.getOrganizationId(),
                terraformJob.getWorkspaceId(), workingDirectory);

        File sshKeyFile = null;
        if (terraformJob.getVcsType().startsWith("SSH")) {
            String sshFileName = terraformJob.getVcsType().split("~")[1];
            String sshFilePath = String.format(SSH_DIRECTORY, FileUtils.getUserDirectoryPath(), terraformJob.getOrganizationId(), terraformJob.getWorkspaceId(), sshFileName);
            sshKeyFile = new File(sshFilePath);
        }

        TerraformProcessData terraformProcessData = TerraformProcessData.builder()
                .terraformVersion(terraformJob.getTerraformVersion())
                .terraformVariables(terraformJob.getVariables())
                .terraformEnvironmentVariables(terraformJob.getEnvironmentVariables())
                .workingDirectory(workingDirectory)
                .sshFile(sshKeyFile)
                .build();

        return terraformProcessData;
    }
}