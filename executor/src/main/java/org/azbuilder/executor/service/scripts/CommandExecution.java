package org.azbuilder.executor.service.scripts;

import org.azbuilder.executor.service.mode.TerraformJob;

import java.io.File;
import java.util.function.Consumer;

public interface CommandExecution {
    boolean execute(TerraformJob terraformJob, String command, File workingDirectory, Consumer<String> output);
}