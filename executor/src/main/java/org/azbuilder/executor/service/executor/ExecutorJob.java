package org.azbuilder.executor.service.executor;

import org.azbuilder.executor.service.mode.TerraformJob;

public interface ExecutorJob {

    void createJob(TerraformJob terraformJob);
}
