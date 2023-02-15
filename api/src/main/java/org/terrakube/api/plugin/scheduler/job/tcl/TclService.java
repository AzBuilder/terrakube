package org.terrakube.api.plugin.scheduler.job.tcl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;
import org.terrakube.api.plugin.scheduler.job.tcl.model.*;
import org.terrakube.api.repository.JobRepository;
import org.terrakube.api.repository.StepRepository;
import org.terrakube.api.repository.TemplateRepository;
import org.terrakube.api.rs.job.Job;
import org.terrakube.api.rs.job.JobStatus;
import org.terrakube.api.rs.job.step.Step;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

@AllArgsConstructor
@Service
@Getter
@Setter
@Slf4j
public class TclService {

    private static final String IMPORT_DIRECTORY = "%s/.terraform-spring-boot/importCommands/%s";

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
        FlowConfig flowConfig = null;
        try {
            FlowConfig temp = yaml.load(new String(Base64.getDecoder().decode(tcl)));
            log.info("FlowConfig: \n {}", temp);
            flowConfig = yaml.load(new String(Base64.getDecoder().decode(tcl)));

            if (flowConfig.getFlow().isEmpty()) {
                log.error("Exception parsing yaml: template with no flows");
                return setErrorFlowYaml("Yaml Template does not have any flow");
            }
        } catch (Exception ex) {
            log.error("Exception parsing yaml: {}", ex.getMessage());
            flowConfig = setErrorFlowYaml(ex.getMessage());
        }
        return flowConfig;
    }

    private FlowConfig setErrorFlowYaml(String message) {
        FlowConfig flowConfig = new FlowConfig();
        List<Flow> flowList = new ArrayList();
        Flow errorFlow = new Flow();
        errorFlow.setType(FlowType.yamlError.toString());
        errorFlow.setStep(100);
        errorFlow.setError(message);
        errorFlow.setName("Template Yaml Error, check API logs");
        flowList.add(errorFlow);
        flowConfig.setFlow(flowList);

        return flowConfig;
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

            ImportComands importComands = nextFlow.get().getImportComands();
            if (importComands != null) {
                log.info("Import commands from {} branch {} folder {}", importComands.getRepository(), importComands.getBranch(), importComands.getFolder());

                nextFlow.get().setCommands(importCommands(importComands.getRepository(), importComands.getBranch(), importComands.getFolder()));
            }

            return nextFlow.isPresent() ? nextFlow.get() : null;
        } else
            return null;
    }

    private List<Command> importCommands(String repository, String branch, String folder) {
        List<Command> commands = new ArrayList();
        try {
            File importFolder = generateImportFolder();
            String commandsText = getCommandList(repository, branch, folder, generateImportFolder());

            Yaml yaml = new Yaml(new Constructor(CommandConfig.class));
            CommandConfig temp = yaml.load(commandsText);

            commands = temp.getCommands();

            log.info("Importing commands \n{}\n", commandsText);

            FileUtils.cleanDirectory(importFolder);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return commands;
    }

    private String getCommandList(String repository, String branch, String folder, File folderImport) {
        String commandList = "";
        try {
            Git.cloneRepository()
                    .setURI(repository)
                    .setDirectory(folderImport)
                    .setBranch(branch)
                    .call();
            File importData = null;
            if (folder.equals("/")) {
                importData = new File(String.format("%s/commands.yaml", folderImport.getCanonicalPath()));
            } else {
                importData = new File(String.format("%s/%s/commands.yaml", folderImport.getCanonicalPath(), folder));
            }

            commandList = FileUtils.readFileToString(importData, Charset.defaultCharset());
        } catch (IOException | GitAPIException e) {
            log.error(e.getMessage());
        }
        return commandList;
    }

    private File generateImportFolder() {
        String importCommandFolder = String.format(IMPORT_DIRECTORY, FileUtils.getUserDirectoryPath(), UUID.randomUUID());
        File importFolder = new File(importCommandFolder);
        try {
            if (!importFolder.exists()) {
                log.info("Creating new import folder for {}", importCommandFolder);
                FileUtils.forceMkdir(importFolder);
            } else {
                FileUtils.cleanDirectory(importFolder);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return importFolder;
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
