package org.terrakube.executor.service.terraform;

import org.terrakube.executor.service.executor.ExecutorJobResult;
import org.terrakube.executor.service.mode.TerraformJob;

import java.io.File;
import java.io.IOException;

public interface TerraformExecutor {

    ExecutorJobResult plan(TerraformJob terraformJob, File workingDirectory, boolean isDestroy);

    ExecutorJobResult apply(TerraformJob terraformJob, File workingDirectory);

    ExecutorJobResult destroy(TerraformJob terraformJob, File workingDirectory);

    String version();

}
