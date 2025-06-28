package io.terrakube.executor.service.terraform;

import io.terrakube.executor.service.executor.ExecutorJobResult;
import io.terrakube.executor.service.mode.TerraformJob;

import java.io.File;

public interface TerraformExecutor {

    ExecutorJobResult plan(TerraformJob terraformJob, File workingDirectory, boolean isDestroy);

    ExecutorJobResult apply(TerraformJob terraformJob, File workingDirectory);

    ExecutorJobResult destroy(TerraformJob terraformJob, File workingDirectory);

    String version();

}
