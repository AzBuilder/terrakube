package org.azbuilder.executor.service.terraform;

import org.azbuilder.executor.service.executor.ExecutorJobResult;
import org.azbuilder.executor.service.mode.TerraformJob;

import java.io.File;
import java.util.HashMap;

public interface TerraformExecutor {

    ExecutorJobResult plan(TerraformJob terraformJob, File workingDirectory);

    ExecutorJobResult apply(TerraformJob terraformJob, File workingDirectory);

    ExecutorJobResult destroy(TerraformJob terraformJob, File workingDirectory);

    String version();

}
