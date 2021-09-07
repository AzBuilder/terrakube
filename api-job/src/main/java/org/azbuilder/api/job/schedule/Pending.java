package org.azbuilder.api.job.schedule;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.client.RestClient;
import org.azbuilder.api.client.model.organization.Organization;
import org.azbuilder.api.client.model.organization.job.Job;
import org.azbuilder.api.client.model.organization.job.JobRequest;
import org.azbuilder.api.client.model.organization.workspace.Workspace;
import org.azbuilder.api.client.model.organization.workspace.variable.Variable;
import org.azbuilder.api.client.model.response.ResponseWithInclude;
import org.azbuilder.terraform.TerraformCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class Pending {

    @Autowired
    RestClient restClient;

    @Value("${org.azbuilder.executor.url}")
    private String executorUrl;

    @Scheduled(fixedRate = 60000)
    public void pendingJobs() {

        ResponseWithInclude<List<Organization>, Job> organizationJobList = restClient.getAllOrganizationsWithJobStatus("pending");

        if (organizationJobList.getData().size() > 0 && organizationJobList.getIncluded() != null)
            for (Job job : organizationJobList.getIncluded()) {
                log.info("Pending Job: {} WorkspaceId: {}", job.getId(), job.getRelationships().getWorkspace().getData().getId());

                RestTemplate restTemplate = new RestTemplate();
                TerraformJob terraformJob = new TerraformJob();
                terraformJob.setOrganizationId(job.getRelationships().getOrganization().getData().getId());
                terraformJob.setWorkspaceId(job.getRelationships().getWorkspace().getData().getId());
                terraformJob.setJobId(job.getId());

                log.info("Checking Variables");
                ResponseWithInclude<Workspace, Variable> workspaceData = restClient.getWorkspaceByIdWithVariables(terraformJob.getOrganizationId(), terraformJob.getWorkspaceId());

                HashMap<String, String> variables = new HashMap<>();
                HashMap<String, String> environmentVariables = new HashMap<>();
                HashMap<String, String> secrets = new HashMap<>();
                List<Variable> variableList = workspaceData.getIncluded();
                if (variableList != null)
                    for (Variable variable : variableList) {
                        switch (variable.getAttributes().getCategory()) {
                            case "terraform":
                                log.info("Adding terraform");
                                variables.put(variable.getAttributes().getKey(), variable.getAttributes().getValue());
                            case "env":
                                log.info("Adding environment variable");
                                environmentVariables.put(variable.getAttributes().getKey(), variable.getAttributes().getValue());
                                break;
                        }
                        log.info("Variable Key: {} Value {}", variable.getAttributes().getKey(), variable.getAttributes().isSensitive() ? "sensitive" : variable.getAttributes().getValue());
                    }
                terraformJob.setVariables(variables);
                terraformJob.setEnvironmentVariables(environmentVariables);

                terraformJob.setTerraformCommand(TerraformCommand.valueOf(job.getAttributes().getCommand()));
                terraformJob.setTerraformVersion(workspaceData.getData().getAttributes().getTerraformVersion());
                terraformJob.setSource(workspaceData.getData().getAttributes().getSource());
                terraformJob.setBranch(workspaceData.getData().getAttributes().getBranch());

                ResponseEntity<TerraformJob> response = restTemplate.postForEntity(this.executorUrl, terraformJob, TerraformJob.class);

                log.info("Response Status: {}", response.getStatusCode().value());

                if (response.getStatusCode().equals(HttpStatus.ACCEPTED)) {
                    JobRequest jobRequest = new JobRequest();
                    job.getAttributes().setStatus("queue");
                    jobRequest.setData(job);

                    restClient.updateJob(jobRequest, job.getRelationships().getOrganization().getData().getId(), job.getId());
                }
            }
    }
}

@Getter
@Setter
class TerraformJob {

    private TerraformCommand terraformCommand;
    private String organizationId;
    private String workspaceId;
    private String jobId;
    private String terraformVersion;
    private String source;
    private String branch;
    private HashMap<String, String> environmentVariables;
    private HashMap<String, String> variables;
    private HashMap<String, String> secrets;
}

