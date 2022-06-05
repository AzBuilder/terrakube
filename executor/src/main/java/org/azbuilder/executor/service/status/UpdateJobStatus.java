package org.azbuilder.executor.service.status;

import org.azbuilder.executor.service.mode.TerraformJob;

public interface UpdateJobStatus {

    void setRunningStatus(TerraformJob job);

    void setCompletedStatus(boolean successful, TerraformJob job, String jobOutput, String jobErrorOutput, String jobPlan);
}
