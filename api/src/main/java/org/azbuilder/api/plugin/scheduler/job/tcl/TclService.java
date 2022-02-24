package org.azbuilder.api.plugin.scheduler.job.tcl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.scheduler.job.tcl.model.Flow;
import org.azbuilder.api.plugin.scheduler.job.tcl.model.FlowConfig;
import org.azbuilder.api.repository.JobRepository;
import org.azbuilder.api.repository.StepRepository;
import org.azbuilder.api.repository.TemplateRepository;
import org.azbuilder.api.rs.job.Job;
import org.azbuilder.api.rs.job.JobStatus;
import org.azbuilder.api.rs.job.step.Step;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.util.Base64;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;

@AllArgsConstructor
@Component
@Getter
@Setter
@Slf4j
public class TclService {

    JobRepository jobRepository;
    StepRepository stepRepository;
    TemplateRepository templateRepository;

    @Transactional
    public Job initJobConfiguration(Job job) {
        log.info("InitialJobSetup {}", job.getId());
        if (job.getStep().isEmpty()) {

            FlowConfig flowConfig = null;
            if (job.getTemplateReference() != null) {
                String tcl = getTemplateTcl(job.getTemplateReference());
                job.setTcl(tcl);
                jobRepository.save(job);
                flowConfig = getFlowConfig(tcl);
            } else {
                flowConfig = getFlowConfig(job.getTcl());
            }
            log.info("Custom Job Setup: \n {}", flowConfig.toString());

            flowConfig.getFlow().parallelStream().forEach(flow -> {
                log.info("Creating step: {}", flow.toString());
                Step newStep = new Step();
                newStep.setStatus(JobStatus.pending);
                newStep.setStepNumber(flow.getStep());
                if (flow.getName() != null) {
                    newStep.setName(flow.getName());
                } else {
                    newStep.setName("Running Step" + flow.getStep());
                }
                log.info("Step name {}", newStep.getName());
                Job parentJob = jobRepository.getById(job.getId());
                newStep.setJob(parentJob);
                newStep = stepRepository.save(newStep);
                log.info("Parent {} Step created {}", newStep.getJob().getId(), newStep.getId());
            });
            return jobRepository.getById(job.getId());
        } else
            return job;
    }

    private FlowConfig getFlowConfig(String tcl) {
        Yaml yaml = new Yaml(new Constructor(FlowConfig.class));
        FlowConfig temp = yaml.load(new String(Base64.getDecoder().decode(tcl)));
        log.info("FlowConfig: \n {}", temp);
        return yaml.load(new String(Base64.getDecoder().decode(tcl)));
    }

    public Flow getNextFlow(Job job) {
        TreeMap<Integer, Step> map = getPendingSteps(job);

        if (!map.isEmpty()) {
            log.info("Next Command: {}", map.firstKey());
            Optional<Flow> nextFlow = getFlowConfig(job.getTcl())
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
        stepRepository.findByJobId(job.getId())
                .stream()
                .filter(step -> step.getStatus().equals(JobStatus.pending))
                .forEach(step -> map.put(Integer.valueOf(step.getStepNumber()), step));
        log.info("Pending steps {}", map.size());
        return map;
    }

    public String getCurrentStepId(Job job) {
        return getPendingSteps(job).firstEntry().getValue().getId().toString();
    }

    private String getTemplateTcl(String templateId) {
        return templateRepository.getById(UUID.fromString(templateId)).getTcl();
    }
}
