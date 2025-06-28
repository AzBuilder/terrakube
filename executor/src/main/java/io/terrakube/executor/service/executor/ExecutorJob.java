package io.terrakube.executor.service.executor;

import io.terrakube.executor.service.mode.TerraformJob;

public interface ExecutorJob {

    void createJob(TerraformJob terraformJob);
}
