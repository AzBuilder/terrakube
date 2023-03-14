package org.terrakube.api.plugin.scheduler.job.tcl.executor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClientException;
import org.terrakube.api.plugin.scheduler.job.tcl.model.Flow;
import org.terrakube.api.repository.JobRepository;
import org.terrakube.api.rs.globalvar.Globalvar;
import org.terrakube.api.rs.job.Job;
import org.terrakube.api.rs.job.JobStatus;
import org.terrakube.api.rs.ssh.Ssh;
import org.terrakube.api.rs.vcs.Vcs;
import org.terrakube.api.rs.workspace.parameters.Category;
import org.terrakube.api.rs.workspace.parameters.Variable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class ExecutorService {

    @Value("${org.terrakube.executor.url}")
    private String executorUrl;

    @Autowired
    JobRepository jobRepository;

    @Transactional
    public boolean execute(Job job, String stepId, Flow flow) {
        log.info("Pending Job: {} WorkspaceId: {}", job.getId(), job.getWorkspace().getId());

        ExecutorContext executorContext = new ExecutorContext();
        executorContext.setOrganizationId(job.getOrganization().getId().toString());
        executorContext.setWorkspaceId(job.getWorkspace().getId().toString());
        executorContext.setJobId(String.valueOf(job.getId()));
        executorContext.setStepId(stepId);

        log.info("Checking Variables");
        if (job.getWorkspace().getVcs() != null) {
            Vcs vcs = job.getWorkspace().getVcs();
            executorContext.setVcsType(vcs.getVcsType().toString());
            executorContext.setAccessToken(vcs.getAccessToken());
            log.info("Private Repository {}", executorContext.getVcsType());
        } else if (job.getWorkspace().getSsh() != null) {
            Ssh ssh = job.getWorkspace().getSsh();
            executorContext.setVcsType(String.format("SSH~%s", ssh.getSshType().getFileName()));
            executorContext.setAccessToken(ssh.getPrivateKey());
            log.info("Private Repository using SSH private key");
        } else {
            executorContext.setVcsType("PUBLIC");
            log.info("Public Repository");
        }

        HashMap<String, String> variables = new HashMap<>();
        HashMap<String, String> environmentVariables = new HashMap<>();
        environmentVariables.put("TF_IN_AUTOMATION", "1");
        environmentVariables.put("workspaceName", job.getWorkspace().getName());
        environmentVariables.put("organizationName", job.getOrganization().getName());
        List<Variable> variableList = job.getWorkspace().getVariable();
        if (variableList != null)
            for (Variable variable : variableList) {
                if (variable.getCategory().equals(Category.TERRAFORM)) {
                    log.info("Adding terraform");
                    variables.put(variable.getKey(), variable.getValue());
                } else {
                    log.info("Adding environment variable");
                    environmentVariables.put(variable.getKey(), variable.getValue());
                }
                log.info("Variable Key: {} Value {}", variable.getKey(), variable.isSensitive() ? "sensitive" : variable.getValue());
            }

        environmentVariables = loadEnvironmentVariables(job, flow, variables);
        variables = loadTerraformVariables(job, flow, variables);

        executorContext.setVariables(variables);
        executorContext.setEnvironmentVariables(environmentVariables);

        executorContext.setCommandList(flow.getCommands());
        executorContext.setType(flow.getType());
        executorContext.setTerraformVersion(job.getWorkspace().getTerraformVersion());
        executorContext.setSource(job.getWorkspace().getSource());
        executorContext.setBranch(job.getWorkspace().getBranch());
        executorContext.setFolder(job.getWorkspace().getFolder());
        return sendToExecutor(job, executorContext);
    }

    private boolean sendToExecutor(Job job, ExecutorContext executorContext) {
        RestTemplate restTemplate = new RestTemplate();
        boolean executed = false;
        try {
            ResponseEntity<ExecutorContext> response = restTemplate.postForEntity(this.executorUrl, executorContext, ExecutorContext.class);
            executorContext.setAccessToken("****");
            log.info("Sending Job: /n {}", executorContext);
            log.info("Response Status: {}", response.getStatusCode().value());

            if (response.getStatusCode().equals(HttpStatus.ACCEPTED)) {
                job.setStatus(JobStatus.queue);
                jobRepository.save(job);
                executed = true;
            } else
                executed = false;
        } catch (RestClientException ex) {
            log.error(ex.getMessage());
            executed = false;
        }

        return executed;
    }

    private HashMap<String, String> loadEnvironmentVariables(Job job, Flow flow, HashMap<String, String> workspaceEnvVariables) {
        if (flow.getInputsEnv() != null || (flow.getImportComands() != null && flow.getImportComands().getInputsEnv() != null)) {

        } else {
            workspaceEnvVariables = loadDefault(job, Category.ENV, workspaceEnvVariables);
        }
        return workspaceEnvVariables;
    }

    private HashMap<String, String> loadTerraformVariables(Job job, Flow flow, HashMap<String, String> workspaceTerraformVariables) {
        if (flow.getInputsTerraform() != null || (flow.getImportComands() != null && flow.getImportComands().getInputsTerraform() != null)) {

        } else {
            workspaceTerraformVariables = loadDefault(job, Category.TERRAFORM, workspaceTerraformVariables);
        }
        return workspaceTerraformVariables;
    }

    private HashMap<String, String> loadDefault(Job job, Category category, HashMap<String, String> workspaceData){
        if (job.getOrganization().getGlobalvar() != null)
            for (Globalvar globalvar : job.getOrganization().getGlobalvar()) {
                if (globalvar.getCategory().equals(category)) {
                    workspaceData.putIfAbsent(globalvar.getKey(), globalvar.getValue());
                }
                log.info("Adding {} Variable Key: {} Value {}", category, globalvar.getKey(), globalvar.isSensitive() ? "sensitive" : globalvar.getValue());
            }
        return workspaceData;
    }
}
