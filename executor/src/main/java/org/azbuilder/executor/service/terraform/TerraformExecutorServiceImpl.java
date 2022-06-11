package org.azbuilder.executor.service.terraform;

import com.diogonunes.jcolor.AnsiFormat;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.TextStringBuilder;
import org.azbuilder.executor.plugin.tfstate.TerraformState;
import org.azbuilder.executor.service.executor.ExecutorJobResult;
import org.azbuilder.executor.service.mode.TerraformJob;
import org.azbuilder.executor.service.scripts.ScriptEngineService;
import org.jetbrains.annotations.NotNull;
import org.terrakube.terraform.TerraformClient;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.diogonunes.jcolor.Ansi.colorize;
import static com.diogonunes.jcolor.Attribute.*;

@AllArgsConstructor
@Slf4j
@Service
public class TerraformExecutorServiceImpl implements TerraformExecutor {

    private static final String STEP_SEPARATOR = "***************************************";

    TerraformClient terraformClient;
    TerraformState terraformState;
    ScriptEngineService scriptEngineService;

    @Override
    public ExecutorJobResult plan(TerraformJob terraformJob, File workingDirectory) {
        ExecutorJobResult result = new ExecutorJobResult();

        TextStringBuilder jobOutput = new TextStringBuilder();
        TextStringBuilder jobErrorOutput = new TextStringBuilder();
        try {
            Consumer<String> output = getStringConsumer(jobOutput);
            Consumer<String> errorOutput = getStringConsumer(jobErrorOutput);
            HashMap<String, String> terraformParameters = getWorkspaceParameters(terraformJob.getVariables());
            HashMap<String, String> environmentVariables = getWorkspaceParameters(terraformJob.getEnvironmentVariables());
            boolean execution = false;
            boolean scriptBeforeSuccess = true;
            boolean scriptAfterSuccess = true;

            String backendFile = executeTerraformInit(
                    terraformJob,
                    workingDirectory,
                    output,
                    errorOutput);

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

            showTerraformMessage("PLAN", output);
            if (scriptBeforeSuccess)
                execution = terraformClient.plan(
                        terraformJob.getTerraformVersion(),
                        workingDirectory,
                        backendFile,
                        terraformParameters,
                        environmentVariables,
                        output,
                        errorOutput).get();

            if (execution && terraformJob.getCommandList() != null)
                scriptAfterSuccess = scriptEngineService.execute(
                        terraformJob,
                        terraformJob
                                .getCommandList()
                                .stream()
                                .filter(command -> command.isAfter())
                                .collect(Collectors.toCollection(LinkedList::new)),
                        workingDirectory,
                        output);

            result.setPlanFile(execution ? terraformState.saveTerraformPlan(terraformJob.getOrganizationId(), terraformJob.getWorkspaceId(), terraformJob.getJobId(), terraformJob.getStepId(), workingDirectory):"");
            result.setSuccessfulExecution(scriptAfterSuccess);
            result.setOutputLog(jobOutput.toString());
            result.setOutputErrorLog(jobErrorOutput.toString());
        } catch (IOException | ExecutionException | InterruptedException exception) {
            result = setError(exception);
        }
        return result;

    }

    @Override
    public ExecutorJobResult apply(TerraformJob terraformJob, File workingDirectory) {
        ExecutorJobResult result = new ExecutorJobResult();

        TextStringBuilder terraformOutput = new TextStringBuilder();
        TextStringBuilder terraformErrorOutput = new TextStringBuilder();
        try {
            Consumer<String> output = getStringConsumer(terraformOutput);
            Consumer<String> errorOutput = getStringConsumer(terraformErrorOutput);
            HashMap<String, String> terraformParameters = getWorkspaceParameters(terraformJob.getVariables());
            HashMap<String, String> environmentVariables = getWorkspaceParameters(terraformJob.getEnvironmentVariables());
            boolean execution = false;
            boolean scriptBeforeSuccess = true;
            boolean scriptAfterSuccess = true;

            String backendFile = executeTerraformInit(
                    terraformJob,
                    workingDirectory,
                    output,
                    errorOutput);

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

            showTerraformMessage("APPLY", output);
            if (scriptBeforeSuccess) {
                execution = terraformClient.apply(
                        terraformJob.getTerraformVersion(),
                        workingDirectory,
                        backendFile,
                        (terraformState.downloadTerraformPlan(terraformJob.getOrganizationId(), terraformJob.getWorkspaceId(), terraformJob.getJobId(), terraformJob.getStepId(), workingDirectory) ? new HashMap<>() : terraformParameters),
                        environmentVariables,
                        output,
                        errorOutput).get();

                handleTerraformStateChange(terraformJob, workingDirectory);

            }


            if (execution && terraformJob.getCommandList() != null)
                scriptAfterSuccess = scriptEngineService.execute(
                        terraformJob,
                        terraformJob
                                .getCommandList()
                                .stream()
                                .filter(command -> command.isAfter())
                                .collect(Collectors.toCollection(LinkedList::new)),
                        workingDirectory,
                        output);

            result.setSuccessfulExecution(scriptAfterSuccess);
            result.setOutputLog(terraformOutput.toString());
            result.setOutputErrorLog(terraformErrorOutput.toString());
        } catch (IOException | ExecutionException | InterruptedException exception) {
            result = setError(exception);
        }
        return result;
    }

    @Override
    public ExecutorJobResult destroy(TerraformJob terraformJob, File workingDirectory) {
        ExecutorJobResult result = new ExecutorJobResult();

        TextStringBuilder jobOutput = new TextStringBuilder();
        TextStringBuilder jobErrorOutput = new TextStringBuilder();
        try {
            Consumer<String> output = getStringConsumer(jobOutput);
            Consumer<String> errorOutput = getStringConsumer(jobErrorOutput);
            HashMap<String, String> terraformParameters = getWorkspaceParameters(terraformJob.getVariables());
            HashMap<String, String> environmentVariables = getWorkspaceParameters(terraformJob.getEnvironmentVariables());
            boolean execution = false;
            boolean scriptBeforeSuccess = true;
            boolean scriptAfterSuccess = true;

            String backendFile = executeTerraformInit(
                    terraformJob,
                    workingDirectory,
                    output,
                    errorOutput);

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

            showTerraformMessage("DESTROY", output);
            if (scriptBeforeSuccess) {
                execution = terraformClient.destroy(
                        terraformJob.getTerraformVersion(),
                        workingDirectory,
                        backendFile,
                        terraformParameters,
                        environmentVariables,
                        output,
                        errorOutput).get();

                handleTerraformStateChange(terraformJob, workingDirectory);
            }

            if (execution && terraformJob.getCommandList() != null)
                scriptAfterSuccess = scriptEngineService.execute(
                        terraformJob,
                        terraformJob
                                .getCommandList()
                                .stream()
                                .filter(command -> command.isAfter())
                                .collect(Collectors.toCollection(LinkedList::new)),
                        workingDirectory,
                        output);

            result.setSuccessfulExecution(scriptAfterSuccess);
            result.setOutputLog(jobOutput.toString());
            result.setOutputErrorLog(jobErrorOutput.toString());
        } catch (IOException | ExecutionException | InterruptedException exception) {
            result = setError(exception);
        }
        return result;
    }

    private void handleTerraformStateChange(TerraformJob terraformJob, File workingDirectory) throws IOException, ExecutionException, InterruptedException {
        log.info("Running Terraform show");
        TextStringBuilder jsonState = new TextStringBuilder();
        Consumer<String> applyJSON = getStringConsumer(jsonState);
        if (terraformClient.show(terraformJob.getTerraformVersion(), workingDirectory, null, applyJSON, applyJSON).get()) {
            log.info("Uploading terraform state json");
            terraformState.saveStateJson(terraformJob, jsonState.toString());

            TextStringBuilder jsonOutput = new TextStringBuilder();
            Consumer<String> terraformJsonOutput = getStringConsumer(jsonOutput);

            log.info("Checking terraform output json");
            if (terraformClient.output(terraformJob.getTerraformVersion(), workingDirectory, terraformJsonOutput, terraformJsonOutput).get())
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
        ExecutorJobResult error = new ExecutorJobResult();
        error.setSuccessfulExecution(false);
        error.setOutputLog("");
        error.setOutputErrorLog(exception.getMessage());
        log.error(exception.getMessage());

        if (exception instanceof InterruptedException)
            Thread.currentThread().interrupt();
        return error;
    }


    private String executeTerraformInit(TerraformJob terraformJob, File workingDirectory, Consumer<String> output, Consumer<String> errorOutput) throws IOException, ExecutionException, InterruptedException {

        initBanner(terraformJob, output);

        String backendFile = terraformState.getBackendStateFile(terraformJob.getOrganizationId(), terraformJob.getWorkspaceId(), workingDirectory);

        terraformClient.init(terraformJob.getTerraformVersion(), workingDirectory, backendFile, output, errorOutput).get();
        return backendFile;
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
        output.accept(colorize("Initializing Terrakube Job " + terraformJob.getJobId() + " Step " + terraformJob.getStepId(), colorMessage));
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
}