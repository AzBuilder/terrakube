package org.terrakube.executor.service.status;

import org.terrakube.executor.service.mode.TerraformJob;

public interface UpdateJobStatus {

    void setRunningStatus(TerraformJob job);

    void setCompletedStatus(boolean successful, TerraformJob job, String jobOutput, String jobErrorOutput, String jobPlan);
}
