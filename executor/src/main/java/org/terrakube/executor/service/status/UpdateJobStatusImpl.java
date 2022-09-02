package org.terrakube.executor.service.status;

import lombok.extern.slf4j.Slf4j;
import org.terrakube.client.TerrakubeClient;
import org.terrakube.client.model.organization.job.Job;
import org.terrakube.client.model.organization.job.JobRequest;
import org.terrakube.client.model.organization.job.step.Step;
import org.terrakube.client.model.organization.job.step.StepAttributes;
import org.terrakube.client.model.organization.job.step.StepRequest;
import org.terrakube.executor.configuration.ExecutorFlagsProperties;
import org.terrakube.executor.plugin.tfoutput.TerraformOutput;
import org.terrakube.executor.service.mode.TerraformJob;
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
            updateStepStatus(successful, terraformJob.getOrganizationId(), terraformJob.getJobId(), terraformJob.getStepId(), jobOutput, jobErrorOutput);
            if(!isJobCancelled(terraformJob))
                updateJobStatus(successful, terraformJob.getOrganizationId(), terraformJob.getJobId(), terraformJob.getStepId(), jobOutput, jobErrorOutput, jobPlan);
        }
    }

    private boolean isJobCancelled(TerraformJob terraformJob){
        Job job = terrakubeClient.getJobById(terraformJob.getOrganizationId(), terraformJob.getJobId()).getData();
        if(job.getAttributes().getStatus().equals("cancelled")) {
            log.warn("Job {} was cancelled when running executor", terraformJob.getJobId());
            return true;
        }
        else {
            log.info("Job {} is still active", terraformJob.getJobId());
            return false;
        }
    }

    private void updateJobStatus(boolean successful, String organizationId, String jobId, String stepId, String jobOutput, String jobErrorOutput, String jobPlan) {
        Job job = terrakubeClient.getJobById(organizationId, jobId).getData();
        job.getAttributes().setStatus(successful ? "pending" : "failed");

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

    private void updateStepStatus(boolean status, String organizationId, String jobId, String stepId, String jobOutput, String jobErrorOutput) {
        StepAttributes stepAttributes = new StepAttributes();
        stepAttributes.setOutput(this.terraformOutput.save(organizationId, jobId, stepId, jobOutput, jobErrorOutput));
        stepAttributes.setStatus(status ? "completed": "failed");

        Step step = new Step();
        step.setId(stepId);
        step.setType("step");
        step.setAttributes(stepAttributes);
        StepRequest stepRequest = new StepRequest();
        stepRequest.setData(step);

        terrakubeClient.updateStep(stepRequest, organizationId, jobId, stepId);
    }
}
