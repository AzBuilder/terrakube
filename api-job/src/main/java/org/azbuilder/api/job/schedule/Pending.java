package org.azbuilder.api.job.schedule;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.client.RestClient;
import org.azbuilder.api.client.model.organization.Organization;
import org.azbuilder.api.client.model.organization.OrganizationResponse;
import org.azbuilder.api.client.model.organization.job.Job;
import org.azbuilder.api.client.model.organization.job.JobRequest;
import org.azbuilder.api.client.model.organization.module.definition.Definition;
import org.azbuilder.api.client.model.organization.workspace.environment.Environment;
import org.azbuilder.api.client.model.organization.workspace.secret.Secret;
import org.azbuilder.api.client.model.organization.workspace.variable.Variable;
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

    @Value("${org.azbuilder.api.url}")
    private String apiUrl;

    @Value("${org.azbuilder.executor.url}")
    private String executorUrl;

    @Scheduled(fixedRate = 60000)
    public void pendingJobs() {
        log.info("Checking Pending Jobs");

        OrganizationResponse<List<Organization>, Job> organizationJobList = restClient.getAllOrganizationsWithJobStatus("pending");

        if (organizationJobList.getData().size() > 0 && organizationJobList.getIncluded() != null)
            for (Job job : organizationJobList.getIncluded()) {
                log.info("Pending Job: {} WorkspaceId: {}", job.getId(), job.getRelationships().getWorkspace().getData().getId());

                RestTemplate restTemplate = new RestTemplate();
                TerraformJob terraformJob = new TerraformJob();
                terraformJob.setOrganizationId(job.getRelationships().getOrganization().getData().getId());
                terraformJob.setWorkspaceId(job.getRelationships().getWorkspace().getData().getId());
                terraformJob.setJobId(job.getId());

                log.info("Checking Variables");
                //GET WORKSPACE BY ID WITH VARIABLES
                HashMap<String, String> variables = new HashMap<>();
                List<Variable> variableList = restClient.getWorkspaceByIdWithVariables(terraformJob.getOrganizationId(), terraformJob.getWorkspaceId()).getIncluded();
                if (variableList != null)
                    for (Variable variable : variableList) {
                        String parameterKey = variable.getAttributes().get("key");
                        String parameterValue = variable.getAttributes().get("value");
                        log.info("Variable Key: {} Value {}", parameterKey, parameterValue);
                        variables.put(parameterKey, parameterValue);
                    }
                terraformJob.setVariables(variables);

                log.info("Checking Secrets");
                //GET WORKSPACE BY ID WITH SECRETS
                HashMap<String, String> secrets = new HashMap<>();
                List<Secret> secretList = restClient.getWorkspaceByIdWithSecrets(terraformJob.getOrganizationId(), terraformJob.getWorkspaceId()).getIncluded();
                if (secretList != null)
                    for (Secret secret : secretList) {
                        String parameterKey = secret.getAttributes().get("key");
                        String parameterValue = secret.getAttributes().get("value");
                        log.info("Secret Key: {} Value {}", parameterKey, parameterValue);
                        secrets.put(parameterKey, parameterValue);
                    }
                terraformJob.setSecrets(secrets);

                log.info("Checking Environments");
                //GET WORKSPACE BY ID WITH ENVIRONMENTS
                HashMap<String, String> environmentsVariables = new HashMap<>();
                List<Environment> environmentVariableList = restClient.getWorkspaceByIdWithEnvironmentVariables(terraformJob.getOrganizationId(), terraformJob.getWorkspaceId()).getIncluded();
                if (environmentVariableList != null)
                    for (Environment environment : environmentVariableList) {
                        String parameterKey = environment.getAttributes().get("key");
                        String parameterValue = environment.getAttributes().get("value");
                        log.info("Environment Variable Key: {} Value {}", parameterKey, parameterValue);
                        environmentsVariables.put(parameterKey, parameterValue);
                    }
                terraformJob.setEnvironmentVariables(environmentsVariables);

                log.info("Checking Definition");
                //GET WORKSPACE BY ID INCLUDE DEFINITION
                Definition definition = restClient.getWorkspaceByIdWithModuleDefinition(terraformJob.getOrganizationId(), terraformJob.getWorkspaceId()).getIncluded().get(0);
                terraformJob.setTerraformCommand(TerraformCommand.valueOf(job.getAttributes().get("command")));
                terraformJob.setTerraformVersion(definition.getAttributes().get("terraformVersion"));
                terraformJob.setSourceSample(definition.getAttributes().get("sourceSample"));

                ResponseEntity<TerraformJob> response = restTemplate.postForEntity(this.executorUrl, terraformJob, TerraformJob.class);

                log.info("Response Status: {}", response.getStatusCode().value());

                if (response.getStatusCode().equals(HttpStatus.ACCEPTED)) {
                    JobRequest jobRequest = new JobRequest();
                    job.getAttributes().replace("status", "queue");
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
    private String sourceSample;
    private HashMap<String, String> environmentVariables;
    private HashMap<String, String> variables;
    private HashMap<String, String> secrets;
}

