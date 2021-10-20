package org.azbuilder.api.schedule;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.client.TerrakubeClient;
import org.azbuilder.api.client.model.organization.Organization;
import org.azbuilder.api.client.model.organization.job.Job;
import org.azbuilder.api.client.model.organization.job.JobRequest;
import org.azbuilder.api.client.model.organization.job.step.Step;
import org.azbuilder.api.client.model.organization.job.step.StepAttributes;
import org.azbuilder.api.client.model.organization.job.step.StepRequest;
import org.azbuilder.api.client.model.organization.vcs.Vcs;
import org.azbuilder.api.client.model.organization.workspace.Workspace;
import org.azbuilder.api.client.model.organization.workspace.variable.Variable;
import org.azbuilder.api.client.model.response.ResponseWithInclude;
import org.azbuilder.api.schedule.dsl.Command;
import org.azbuilder.api.schedule.dsl.FlowConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class Pending {

    @Autowired
    TerrakubeClient terrakubeClient;

    @Value("${org.azbuilder.executor.url}")
    private String executorUrl;

    @Scheduled(fixedRate = 60000)
    public void pendingJobs() {
        searchPendingJobs().parallelStream().forEach(job -> {

            if (job.getRelationships().getStep().getData().isEmpty())
                job = initialJobSetup(job);

            Optional<LinkedList<Command>> command = Optional.ofNullable(getNextCommand(job));
            Optional<String> type = Optional.ofNullable(getNextType(job));
            if (command.isPresent() && type.isPresent()) {
                log.info("Execute command: {} {}", command.get());
                TerraformJob terraformJob = processPendingJob(job, type.get(), command.get());
                updateJobStatus(job, terraformJob);
            } else {
                completeJob(job);
            }
        });
    }

    private TerraformJob processPendingJob(Job job, String type, LinkedList<Command> command) {
        log.info("Pending Job: {} WorkspaceId: {}", job.getId(), job.getRelationships().getWorkspace().getData().getId());

        TerraformJob terraformJob = new TerraformJob();
        terraformJob.setOrganizationId(job.getRelationships().getOrganization().getData().getId());
        terraformJob.setWorkspaceId(job.getRelationships().getWorkspace().getData().getId());
        terraformJob.setJobId(job.getId());
        terraformJob.setStepId(getCurrentStepId(job));

        log.info("Checking Variables");
        ResponseWithInclude<Workspace, Variable> workspaceData = terrakubeClient.getWorkspaceByIdWithVariables(terraformJob.getOrganizationId(), terraformJob.getWorkspaceId());
        if (workspaceData.getData().getRelationships().getVcs().getData() != null) {
            Vcs vcs = terrakubeClient.getVcsById(job.getRelationships().getOrganization().getData().getId(), workspaceData.getData().getRelationships().getVcs().getData().getId()).getData();
            terraformJob.setVcsType(vcs.getAttributes().getVcsType());
            terraformJob.setAccessToken(vcs.getAttributes().getAccessToken());
            log.info("Private Repository {}", terraformJob.getVcsType());
        } else {
            terraformJob.setVcsType("PUBLIC");
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

        terraformJob.setVariables(variables);
        terraformJob.setEnvironmentVariables(environmentVariables);

        terraformJob.setCommandList(command);
        terraformJob.setType(type);
        terraformJob.setTerraformVersion(workspaceData.getData().getAttributes().getTerraformVersion());
        terraformJob.setSource(workspaceData.getData().getAttributes().getSource());
        terraformJob.setBranch(workspaceData.getData().getAttributes().getBranch());

        return terraformJob;
    }

    private Job initialJobSetup(Job job) {
        if (job.getRelationships().getStep().getData().isEmpty()) {

            FlowConfig tclConfiguration = getFlowConfig(job.getAttributes().getTcl());
            log.info("Custom Job Setup: {}", tclConfiguration.toString());

            AtomicInteger step = new AtomicInteger(0);
            tclConfiguration.getFlow().parallelStream().forEach(flow -> {
                log.info("Creating step: {}", flow.toString());
                StepRequest stepRequest = new StepRequest();
                Step newStep = new Step();
                newStep.setType("step");
                StepAttributes stepAttributes = new StepAttributes();
                stepAttributes.setStatus("pending");
                stepAttributes.setStepNumber(String.valueOf(step.getAndAdd(1)));
                newStep.setAttributes(stepAttributes);
                stepRequest.setData(newStep);
                terrakubeClient.createStep(stepRequest, job.getRelationships().getOrganization().getData().getId(), job.getId());
            });
        }
        return terrakubeClient.getJobById(job.getRelationships().getOrganization().getData().getId(), job.getId()).getData();
    }

    private FlowConfig getFlowConfig(String tcl) {
        Yaml yaml = new Yaml(new Constructor(FlowConfig.class));
        FlowConfig temp = yaml.load(new String(Base64.getDecoder().decode(tcl)));
        log.info("FlowConfig: {}", temp);
        return yaml.load(new String(Base64.getDecoder().decode(tcl)));
    }

    private LinkedList<Command> getNextCommand(Job job) {
        TreeMap<Integer, Step> map = getPendingSteps(job);

        if (!map.isEmpty())
            return getFlowConfig(job.getAttributes().getTcl())
                    .getFlow()
                    .get(map.firstKey()).getCommands();
        else
            return null;
    }

    private String getNextType(Job job) {
        TreeMap<Integer, Step> map = getPendingSteps(job);

        if (!map.isEmpty())
            return getFlowConfig(job.getAttributes().getTcl())
                    .getFlow()
                    .get(map.firstKey()).getType();
        else
            return null;
    }

    private TreeMap<Integer, Step> getPendingSteps(Job job) {
        final TreeMap<Integer, Step> map = new TreeMap<>();
        terrakubeClient.getJobById(job.getRelationships().getOrganization().getData().getId(), job.getId())
                .getIncluded()
                .stream()
                .filter(step -> step.getAttributes().getStatus().equals("pending"))
                .forEach(step -> map.put(Integer.valueOf(step.getAttributes().getStepNumber()), step));
        return map;
    }

    private String getCurrentStepId(Job job) {
        return getPendingSteps(job).firstEntry().getValue().getId();
    }

    private List<Job> searchPendingJobs() {
        ResponseWithInclude<List<Organization>, Job> organizationJobList = terrakubeClient.getAllOrganizationsWithJobStatus("pending");

        if (organizationJobList.getData().size() > 0 && organizationJobList.getIncluded() != null)
            return organizationJobList.getIncluded();
        else
            return new ArrayList<>();
    }

    private void updateJobStatus(Job job, TerraformJob terraformJob) {
        RestTemplate restTemplate = new RestTemplate();
        log.info("Sending Job: {}", terraformJob);
        ResponseEntity<TerraformJob> response = restTemplate.postForEntity(this.executorUrl, terraformJob, TerraformJob.class);

        log.info("Response Status: {}", response.getStatusCode().value());

        if (response.getStatusCode().equals(HttpStatus.ACCEPTED)) {
            JobRequest jobRequest = new JobRequest();
            job.getAttributes().setStatus("queue");
            jobRequest.setData(job);

            terrakubeClient.updateJob(jobRequest, job.getRelationships().getOrganization().getData().getId(), job.getId());
        }
    }

    private void completeJob(Job job) {
        JobRequest jobRequest = new JobRequest();
        job.getAttributes().setStatus("completed");
        jobRequest.setData(job);
        terrakubeClient.updateJob(jobRequest, job.getRelationships().getOrganization().getData().getId(), job.getId());
    }
}

@ToString
@Getter
@Setter
class TerraformJob {
    private LinkedList<Command> commandList;
    private String type;
    private String organizationId;
    private String workspaceId;
    private String jobId;
    private String stepId;
    private String terraformVersion;
    private String source;
    private String branch;
    private String vcsType;
    private String accessToken;
    private HashMap<String, String> environmentVariables;
    private HashMap<String, String> variables;
}

