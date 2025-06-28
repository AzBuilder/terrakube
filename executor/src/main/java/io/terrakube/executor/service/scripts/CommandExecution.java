package io.terrakube.executor.service.scripts;

import io.terrakube.executor.service.mode.TerraformJob;

import java.io.File;
import java.util.function.Consumer;

public interface CommandExecution {
    boolean execute(TerraformJob terraformJob, String command, File terraformWorkingDir, Consumer<String> output);
}