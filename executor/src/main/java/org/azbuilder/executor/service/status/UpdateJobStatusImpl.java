package org.azbuilder.executor.service.status;

import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.client.TerrakubeClient;
import org.azbuilder.api.client.model.organization.job.Job;
import org.azbuilder.api.client.model.organization.job.JobRequest;
import org.azbuilder.api.client.model.organization.job.step.Step;
import org.azbuilder.api.client.model.organization.job.step.StepAttributes;
import org.azbuilder.api.client.model.organization.job.step.StepRequest;
import org.azbuilder.executor.configuration.ExecutorFlagsProperties;
import org.azbuilder.executor.plugin.tfoutput.TerraformOutput;
import org.azbuilder.executor.service.mode.TerraformJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UpdateJobStatusImpl implements UpdateJobStatus {

    @Autowired
    TerrakubeClient terrakubeClient;

    @Autowired
    TerraformOutput terraformOutput;

    @Autowired
    ExecutorFlagsProperties executorFlagsProperties;

    @Override
    public void setRunningStatus(TerraformJob terraformJob) {
        if (!executorFlagsProperties.isDisableAcknowledge()) {
            Job job = terrakubeClient.getJobById(terraformJob.getOrganizationId(), terraformJob.getJobId()).getData();
            job.getAttributes().setStatus("running");

            JobRequest jobRequest = new JobRequest();
            jobRequest.setData(job);

            terrakubeClient.updateJob(jobRequest, job.getRelationships().getOrganization().getData().getId(), job.getId());
        }
    }

    @Override
    public void setCompletedStatus(boolean successful, TerraformJob terraformJob, String jobOutput, String jobErrorOutput, String jobPlan) {
        if (!executorFlagsProperties.isDisableAcknowledge()) {
            updateStepStatus(terraformJob.getOrganizationId(), terraformJob.getJobId(), terraformJob.getStepId(), jobOutput, jobErrorOutput);
            updateJobStatus(successful, terraformJob.getOrganizationId(), terraformJob.getJobId(), terraformJob.getStepId(), jobOutput, jobErrorOutput, jobPlan);
        }
    }

    private void updateJobStatus(boolean successful, String organizationId, String jobId, String stepId, String jobOutput, String jobErrorOutput, String jobPlan) {
        Job job = terrakubeClient.getJobById(organizationId, jobId).getData();
        job.getAttributes().setStatus(successful ? "pending" : "completed");

        log.info("output: {}", jobOutput.length());
        log.info("outputError: {}", jobErrorOutput.length());

        job.getAttributes().setOutput(
                job.getAttributes().getOutput() == null ? "" : job.getAttributes().getOutput() + " Step " + stepId + " completed\n"
        );
        job.getAttributes().setTerraformPlan(jobPlan);

        JobRequest jobRequest = new JobRequest();
        jobRequest.setData(job);

        terrakubeClient.updateJob(jobRequest, job.getRelationships().getOrganization().getData().getId(), job.getId());
    }

    private void updateStepStatus(String organizationId, String jobId, String stepId, String jobOutput, String jobErrorOutput) {
        StepAttributes stepAttributes = new StepAttributes();
        stepAttributes.setOutput(this.terraformOutput.save(organizationId, jobId, stepId, jobOutput, jobErrorOutput));
        stepAttributes.setStatus("completed");

        Step step = new Step();
        step.setId(stepId);
        step.setType("step");
        step.setAttributes(stepAttributes);
        StepRequest stepRequest = new StepRequest();
        stepRequest.setData(step);

        terrakubeClient.updateStep(stepRequest, organizationId, jobId, stepId);
    }
}
