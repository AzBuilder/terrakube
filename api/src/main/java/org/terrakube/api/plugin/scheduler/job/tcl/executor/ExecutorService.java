package org.terrakube.api.plugin.scheduler.job.tcl.executor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClientException;
import org.terrakube.api.plugin.scheduler.job.tcl.model.Flow;
import org.terrakube.api.repository.GlobalVarRepository;
import org.terrakube.api.repository.JobRepository;
import org.terrakube.api.repository.SshRepository;
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

import javax.swing.text.html.Option;
import java.util.*;

@Slf4j
@Service
public class ExecutorService {

    @Value("${org.terrakube.executor.url}")
    private String executorUrl;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    GlobalVarRepository globalVarRepository;

    @Autowired
    SshRepository sshRepository;


    @Transactional
    public boolean execute(Job job, String stepId, Flow flow) {
        log.info("Pending Job: {} WorkspaceId: {}", job.getId(), job.getWorkspace().getId());

        ExecutorContext executorContext = new ExecutorContext();
        executorContext.setOrganizationId(job.getOrganization().getId().toString());
        executorContext.setWorkspaceId(job.getWorkspace().getId().toString());
        executorContext.setJobId(String.valueOf(job.getId()));
        executorContext.setStepId(stepId);

        if(job.getWorkspace().getBranch().equals("remote-content")){
            log.warn("Running remote operation, disable headers");
            executorContext.setShowHeader(false);
        } else {
            log.warn("Running default operation, enable headers");
            executorContext.setShowHeader(true);
        }

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

        HashMap<String, String> terraformVariables = new HashMap<>();
        HashMap<String, String> environmentVariables = new HashMap<>();
        environmentVariables.put("TF_IN_AUTOMATION", "1");
        environmentVariables.put("workspaceName", job.getWorkspace().getName());
        environmentVariables.put("organizationName", job.getOrganization().getName());
        List<Variable> variableList = job.getWorkspace().getVariable();
        if (variableList != null)
            for (Variable variable : variableList) {
                if (variable.getCategory().equals(Category.TERRAFORM)) {
                    log.info("Adding terraform");
                    terraformVariables.put(variable.getKey(), variable.getValue());
                } else {
                    log.info("Adding environment variable");
                    environmentVariables.put(variable.getKey(), variable.getValue());
                }
                log.info("Variable Key: {} Value {}", variable.getKey(), variable.isSensitive() ? "sensitive" : variable.getValue());
            }

        environmentVariables = loadOtherEnvironmentVariables(job, flow, environmentVariables);
        terraformVariables = loadOtherTerraformVariables(job, flow, terraformVariables);

        executorContext.setVariables(terraformVariables);
        executorContext.setEnvironmentVariables(environmentVariables);

        executorContext.setCommandList(flow.getCommands());
        executorContext.setType(flow.getType());
        executorContext.setTerraformVersion(job.getWorkspace().getTerraformVersion());
        if(job.getOverrideSource() == null) {
            executorContext.setSource(job.getWorkspace().getSource());
        } else {
            executorContext.setSource(job.getOverrideSource());
        }
        if(job.getOverrideBranch() == null) {
            executorContext.setBranch(job.getWorkspace().getBranch());
        } else {
            if (job.getOverrideBranch().equals("remote-content")) {
                executorContext.setShowHeader(false);
            }
            executorContext.setBranch(job.getOverrideBranch());
        }

        if(job.getWorkspace().getModuleSshKey() != null) {
            String moduleSshId = job.getWorkspace().getModuleSshKey();
            Optional<Ssh> ssh = sshRepository.findById(UUID.fromString(moduleSshId));
            if(ssh.isPresent()){
                executorContext.setModuleSshKey(ssh.get().getPrivateKey());
            }
        }
        executorContext.setTofu(job.getWorkspace().isTofu());
        executorContext.setCommitId(job.getCommitId());
        executorContext.setFolder(job.getWorkspace().getFolder());
        executorContext.setRefresh(job.isRefresh());
        executorContext.setRefreshOnly(job.isRefreshOnly());
        return sendToExecutor(job, executorContext);
    }

    private boolean sendToExecutor(Job job, ExecutorContext executorContext) {
        RestTemplate restTemplate = new RestTemplate();
        boolean executed = false;
        try {
            ResponseEntity<ExecutorContext> response = restTemplate.postForEntity(this.executorUrl, executorContext, ExecutorContext.class);
            executorContext.setAccessToken("****");
            executorContext.setModuleSshKey("****");
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

    private HashMap<String, String> loadOtherEnvironmentVariables(Job job, Flow flow, HashMap<String, String> workspaceEnvVariables) {
        if (flow.getInputsEnv() != null || (flow.getImportComands() != null && flow.getImportComands().getInputsEnv() != null)) {
            if (flow.getImportComands() != null && flow.getImportComands().getInputsEnv() != null) {
                log.info("Loading ENV inputs from ImportComands");
                workspaceEnvVariables = loadInputData(job, Category.ENV, new HashMap(flow.getImportComands().getInputsEnv()), workspaceEnvVariables);
            }

            if (flow.getInputsEnv() != null) {
                log.info("Loading ENV inputs from InputsEnv");
                workspaceEnvVariables = loadInputData(job, Category.ENV, new HashMap(flow.getInputsEnv()), workspaceEnvVariables);
            }

        } else {
            log.info("Loading default env variables to job");
            workspaceEnvVariables = loadDefault(job, Category.ENV, workspaceEnvVariables);
        }
        return workspaceEnvVariables;
    }

    private HashMap<String, String> loadOtherTerraformVariables(Job job, Flow flow, HashMap<String, String> workspaceTerraformVariables) {
        if (flow.getInputsTerraform() != null || (flow.getImportComands() != null && flow.getImportComands().getInputsTerraform() != null)) {
            if (flow.getImportComands() != null && flow.getImportComands().getInputsTerraform() != null) {
                log.info("Loading TERRAFORM inputs from ImportComands");
                workspaceTerraformVariables = loadInputData(job, Category.TERRAFORM, new HashMap(flow.getImportComands().getInputsTerraform()), workspaceTerraformVariables);
            }

            if (flow.getInputsTerraform() != null) {
                log.info("Loading TERRAFORM inputs from InputsTerraform");
                workspaceTerraformVariables = loadInputData(job, Category.TERRAFORM, new HashMap(flow.getInputsTerraform()), workspaceTerraformVariables);
            }

        } else {
            log.info("Loading default env variables to job");
            workspaceTerraformVariables = loadDefault(job, Category.TERRAFORM, workspaceTerraformVariables);
        }
        return workspaceTerraformVariables;
    }

    private HashMap<String, String> loadInputData(Job job, Category categoryVar, HashMap<String, String> importFrom, HashMap<String, String> importTo) {
        Map<String, String> finalWorkspaceEnvVariables = importTo;
        importFrom.forEach((key, value) -> {
            java.lang.String searchValue = value.replace("$", "");
            Globalvar globalvar = globalVarRepository.getGlobalvarByOrganizationAndCategoryAndKey(job.getOrganization(), categoryVar, searchValue);
            log.info("Searching globalvar {} ({}) in Org {} found {}", searchValue, categoryVar, job.getOrganization().getName(), (globalvar != null) ? true : false);
            if (globalvar != null) {
                finalWorkspaceEnvVariables.putIfAbsent(key, globalvar.getValue());
            }
        });

        return new HashMap(finalWorkspaceEnvVariables);
    }

    private HashMap<String, String> loadDefault(Job job, Category category, HashMap<String, String> workspaceData) {
        if (job.getOrganization().getGlobalvar() != null)
            for (Globalvar globalvar : job.getOrganization().getGlobalvar()) {
                if (globalvar.getCategory().equals(category)) {
                    workspaceData.putIfAbsent(globalvar.getKey(), globalvar.getValue());
                    log.info("Adding {} Variable Key: {} Value {}", category, globalvar.getKey(), globalvar.isSensitive() ? "sensitive" : globalvar.getValue());
                }
            }
        return workspaceData;
    }
}
