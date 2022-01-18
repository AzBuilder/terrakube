package org.azbuilder.api.schedule;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.client.TerrakubeClient;
import org.azbuilder.api.client.model.organization.job.Job;
import org.azbuilder.api.client.model.organization.job.step.Step;
import org.azbuilder.api.client.model.organization.job.step.StepAttributes;
import org.azbuilder.api.client.model.organization.job.step.StepRequest;
import org.azbuilder.api.schedule.yaml.Flow;
import org.azbuilder.api.schedule.yaml.FlowConfig;
import org.azbuilder.api.schedule.yaml.FlowType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.util.*;

@AllArgsConstructor
@Service
@Slf4j
public class ScheduleJobService {

    private static final String JOB_COMPLETED="completed";
    private static final String JOB_WAITING_APPROVAL="waitingApproval";

    TerrakubeClient terrakubeClient;
    JobService jobService;

    @Scheduled(fixedRate = 60000)
    public void searchPendingJobs() {
        jobService.searchPendingJobs().parallelStream().forEach(job -> {

            job = initialJobSetup(job);

            Optional<Flow> flow = Optional.ofNullable(getNextFlow(job));
            if (flow.isPresent()) {
                log.info("Execute command: {} \n {}", flow.get().getType(), flow.get().getCommands());
                String stepId = getCurrentStepId(job);
                FlowType tempFlowType = FlowType.valueOf(flow.get().getType());

                switch (tempFlowType) {
                    case terraformPlan:
                    case terraformApply:
                    case terraformDestroy:
                    case customScripts:
                        if (jobService.execute(job, stepId, flow.get()))
                            log.info("Executing Job {} Step Id {}", job.getId(), stepId);
                        break;
                    case approval:
                        jobService.requireJobApproval(job, JOB_WAITING_APPROVAL, flow.get().getTeam());
                        log.info("Waiting Approval for Job {} Step Id {}", job.getId(), stepId);
                        break;
                    default:
                        log.error("FlowType not supported");
                        break;
                }

            } else {
                jobService.completeJob(job);
            }
        });
    }

    @Scheduled(fixedRate = 60000)
    public void searchApprovedJobs() {
        jobService.searchApprovedJobs().parallelStream().forEach(job -> {

            job = initialJobSetup(job);

            Optional<Flow> flow = Optional.ofNullable(getNextFlow(job));
            if (flow.isPresent()) {
                log.info("Execute command: {} \n {}", flow.get().getType(), flow.get().getCommands());
                String stepId = getCurrentStepId(job);
                jobService.removeApprovalTeam(job);
                if (jobService.execute(job, stepId, flow.get()))
                    log.info("Executing Job {} Step Id {}", job.getId(), stepId);
            }
        });
    }

    private Job initialJobSetup(Job job) {
        if (job.getRelationships().getStep().getData().isEmpty()) {

            FlowConfig flowConfig = getFlowConfig(job.getAttributes().getTcl());
            log.info("Custom Job Setup: \n {}", flowConfig.toString());

            flowConfig.getFlow().parallelStream().forEach(flow -> {
                log.info("Creating step: {}", flow.toString());
                StepRequest stepRequest = new StepRequest();
                Step newStep = new Step();
                newStep.setType("step");
                StepAttributes stepAttributes = new StepAttributes();
                stepAttributes.setStatus("pending");
                stepAttributes.setStepNumber(String.valueOf(flow.getStep()));
                log.info("Step name {}", flow.getName());
                if(flow.getName()!=null) {
                    stepAttributes.setName(flow.getName());
                }else{
                    stepAttributes.setName("Running "+ flow.getStep());
                }
                newStep.setAttributes(stepAttributes);
                stepRequest.setData(newStep);
                terrakubeClient.createStep(stepRequest, job.getRelationships().getOrganization().getData().getId(), job.getId());
            });
            return terrakubeClient.getJobById(job.getRelationships().getOrganization().getData().getId(), job.getId()).getData();
        } else
            return job;
    }


    private FlowConfig getFlowConfig(String tcl) {
        Yaml yaml = new Yaml(new Constructor(FlowConfig.class));
        FlowConfig temp = yaml.load(new String(Base64.getDecoder().decode(tcl)));
        log.info("FlowConfig: \n {}", temp);
        return yaml.load(new String(Base64.getDecoder().decode(tcl)));
    }

    private Flow getNextFlow(Job job) {
        TreeMap<Integer, Step> map = getPendingSteps(job);

        if (!map.isEmpty()) {
            log.info("Next Command: {}", map.firstKey());
            Optional<Flow> nextFlow = getFlowConfig(job.getAttributes().getTcl())
                    .getFlow()
                    .stream()
                    .filter(flow -> flow.getStep() == map.firstKey())
                    .findFirst();
            return nextFlow.isPresent() ? nextFlow.get() : null;
        } else
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

    public String getCurrentStepId(Job job) {
        return getPendingSteps(job).firstEntry().getValue().getId();
    }
}

