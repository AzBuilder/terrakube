package org.azbuilder.api.plugin.scheduler.job.tcl.executor;

import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.scheduler.job.tcl.model.Flow;
import org.azbuilder.api.repository.JobRepository;
import org.azbuilder.api.rs.job.Job;
import org.azbuilder.api.rs.job.JobStatus;
import org.azbuilder.api.rs.vcs.Vcs;
import org.azbuilder.api.rs.workspace.parameters.Category;
import org.azbuilder.api.rs.workspace.parameters.Variable;
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

    @Value("${org.azbuilder.executor.url}")
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
        } else {
            executorContext.setVcsType("PUBLIC");
            log.info("Public Repository");
        }

        HashMap<String, String> variables = new HashMap<>();
        HashMap<String, String> environmentVariables = new HashMap<>();
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

        executorContext.setVariables(variables);
        executorContext.setEnvironmentVariables(environmentVariables);

        executorContext.setCommandList(flow.getCommands());
        executorContext.setType(flow.getType());
        executorContext.setTerraformVersion(job.getWorkspace().getTerraformVersion());
        executorContext.setSource(job.getWorkspace().getSource());
        executorContext.setBranch(job.getWorkspace().getBranch());

        return sendToExecutor(job, executorContext);
    }

    private boolean sendToExecutor(Job job, ExecutorContext executorContext) {
        log.info("Sending Job: /n {}", executorContext);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ExecutorContext> response = restTemplate.postForEntity(this.executorUrl, executorContext, ExecutorContext.class);

        log.info("Response Status: {}", response.getStatusCode().value());

        if (response.getStatusCode().equals(HttpStatus.ACCEPTED)) {
            job.setStatus(JobStatus.queue);
            jobRepository.save(job);
            return true;
        } else
            return false;
    }
}
