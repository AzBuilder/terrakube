package org.terrakube.executor.service.executor;

import org.terrakube.executor.service.mode.TerraformJob;

public interface ExecutorJob {

    void createJob(TerraformJob terraformJob);
}
