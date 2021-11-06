package org.azbuilder.api.schedule;

import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.client.TerrakubeClient;
import org.azbuilder.api.client.model.organization.Organization;
import org.azbuilder.api.client.model.organization.job.Job;
import org.azbuilder.api.client.model.organization.job.JobRequest;
import org.azbuilder.api.client.model.organization.vcs.Vcs;
import org.azbuilder.api.client.model.organization.workspace.Workspace;
import org.azbuilder.api.client.model.organization.workspace.variable.Variable;
import org.azbuilder.api.client.model.response.ResponseWithInclude;
import org.azbuilder.api.schedule.executor.ExecutorJob;
import org.azbuilder.api.schedule.yaml.Flow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class JobService {

    @Autowired
    TerrakubeClient terrakubeClient;

    @Value("${org.azbuilder.executor.url}")
    private String executorUrl;

    public boolean execute(Job job, String stepId, Flow flow) {
        log.info("Pending Job: {} WorkspaceId: {}", job.getId(), job.getRelationships().getWorkspace().getData().getId());

        ExecutorJob executorJob = new ExecutorJob();
        executorJob.setOrganizationId(job.getRelationships().getOrganization().getData().getId());
        executorJob.setWorkspaceId(job.getRelationships().getWorkspace().getData().getId());
        executorJob.setJobId(job.getId());
        executorJob.setStepId(stepId);

        log.info("Checking Variables");
        ResponseWithInclude<Workspace, Variable> workspaceData = terrakubeClient.getWorkspaceByIdWithVariables(executorJob.getOrganizationId(), executorJob.getWorkspaceId());
        if (workspaceData.getData().getRelationships().getVcs().getData() != null) {
            Vcs vcs = terrakubeClient.getVcsById(job.getRelationships().getOrganization().getData().getId(), workspaceData.getData().getRelationships().getVcs().getData().getId()).getData();
            executorJob.setVcsType(vcs.getAttributes().getVcsType());
            executorJob.setAccessToken(vcs.getAttributes().getAccessToken());
            log.info("Private Repository {}", executorJob.getVcsType());
        } else {
            executorJob.setVcsType("PUBLIC");
            log.info("Public Repository");
        }

        HashMap<String, String> variables = new HashMap<>();
        HashMap<String, String> environmentVariables = new HashMap<>();
        List<Variable> variableList = workspaceData.getIncluded();
        if (variableList != null)
            for (Variable variable : variableList) {
                if (variable.getAttributes().getCategory().equals("TERRAFORM")) {
                    log.info("Adding terraform");
                    variables.put(variable.getAttributes().getKey(), variable.getAttributes().getValue());
                } else {
                    log.info("Adding environment variable");
                    environmentVariables.put(variable.getAttributes().getKey(), variable.getAttributes().getValue());
                }
                log.info("Variable Key: {} Value {}", variable.getAttributes().getKey(), variable.getAttributes().isSensitive() ? "sensitive" : variable.getAttributes().getValue());
            }

        executorJob.setVariables(variables);
        executorJob.setEnvironmentVariables(environmentVariables);

        executorJob.setCommandList(flow.getCommands());
        executorJob.setType(flow.getType());
        executorJob.setTerraformVersion(workspaceData.getData().getAttributes().getTerraformVersion());
        executorJob.setSource(workspaceData.getData().getAttributes().getSource());
        executorJob.setBranch(workspaceData.getData().getAttributes().getBranch());

        return sendToExecutor(job, executorJob);
    }

    public void requireJobApproval(Job job, String newStatus, String approvalTeam) {
        JobRequest jobRequest = new JobRequest();
        job.getAttributes().setStatus(newStatus);
        //job.getAttributes().setApprovalTeam(approvalTeam);
        jobRequest.setData(job);
        terrakubeClient.updateJob(jobRequest, job.getRelationships().getOrganization().getData().getId(), job.getId());
    }

    public List<Job> searchPendingJobs() {
        ResponseWithInclude<List<Organization>, Job> organizationJobList = terrakubeClient.getAllOrganizationsWithJobStatus("pending");

        if (!organizationJobList.getData().isEmpty() && organizationJobList.getIncluded() != null)
            return organizationJobList.getIncluded();
        else
            return new ArrayList<>();
    }

    public List<Job> searchApprovedJobs() {
        ResponseWithInclude<List<Organization>, Job> organizationJobList = terrakubeClient.getAllOrganizationsWithJobStatus("approved");

        if (!organizationJobList.getData().isEmpty() && organizationJobList.getIncluded() != null)
            return organizationJobList.getIncluded();
        else
            return new ArrayList<>();
    }

    public void completeJob(Job job) {
        JobRequest jobRequest = new JobRequest();
        job.getAttributes().setStatus("completed");
        jobRequest.setData(job);
        terrakubeClient.updateJob(jobRequest, job.getRelationships().getOrganization().getData().getId(), job.getId());
    }

    private boolean sendToExecutor(Job job, ExecutorJob executorJob) {
        log.info("Sending Job: /n {}", executorJob);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ExecutorJob> response = restTemplate.postForEntity(this.executorUrl, executorJob, ExecutorJob.class);

        log.info("Response Status: {}", response.getStatusCode().value());

        if (response.getStatusCode().equals(HttpStatus.ACCEPTED)) {
            JobRequest jobRequest = new JobRequest();
            job.getAttributes().setStatus("queue");
            jobRequest.setData(job);
            terrakubeClient.updateJob(jobRequest, job.getRelationships().getOrganization().getData().getId(), job.getId());
            return true;
        } else
            return false;
    }
}
