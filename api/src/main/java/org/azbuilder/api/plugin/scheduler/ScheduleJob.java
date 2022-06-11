package org.azbuilder.api.plugin.scheduler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.scheduler.job.tcl.executor.ExecutorService;
import org.azbuilder.api.plugin.scheduler.job.tcl.TclService;
import org.azbuilder.api.plugin.scheduler.job.tcl.model.Flow;
import org.azbuilder.api.plugin.scheduler.job.tcl.model.FlowType;
import org.azbuilder.api.repository.JobRepository;
import org.azbuilder.api.repository.StepRepository;
import org.azbuilder.api.rs.job.Job;
import org.azbuilder.api.rs.job.JobStatus;
import org.azbuilder.api.rs.job.step.Step;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.azbuilder.api.plugin.scheduler.ScheduleJobService.PREFIX_JOB_CONTEXT;

@AllArgsConstructor
@Component
@Getter
@Setter
@Slf4j
public class ScheduleJob implements org.quartz.Job {

    public static final String JOB_ID = "jobId";

    JobRepository jobRepository;

    StepRepository stepRepository;
    TclService tclService;
    ExecutorService executorService;

    @Transactional
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        int jobId = jobExecutionContext.getJobDetail().getJobDataMap().getInt(JOB_ID);
        Job job = jobRepository.getById(jobId);

        log.info("Checking Job {} Status {}", job.getId(), job.getStatus());

        switch (job.getStatus()) {
            case pending:
                executePendingJob(job);
                break;
            case approved:
                executeApprovedJobs(job);
                break;
            case running:
                log.info("Job {} running", job.getId());
                break;
            case completed:
                try {
                    log.info("Deleting Job Context {}", PREFIX_JOB_CONTEXT + job.getId());
                    jobExecutionContext.getScheduler().deleteJob(new JobKey(PREFIX_JOB_CONTEXT + job.getId()));
                } catch (SchedulerException e) {
                    log.error(e.getMessage());
                }
                break;
            case cancelled:
            case failed:
            case rejected:
                try {
                    log.info("Deleting Failed/Cancelled/Rejected Job Context {} from Quartz", PREFIX_JOB_CONTEXT + job.getId());
                    cancelJobSteps(job.getId());
                    jobExecutionContext.getScheduler().deleteJob(new JobKey(PREFIX_JOB_CONTEXT + job.getId()));
                } catch (SchedulerException e) {
                    log.error(e.getMessage());
                }
                break;
            default:
                log.info("Job {} Status {}", job.getId(), job.getStatus());
                break;
        }
    }

    private void executePendingJob(Job job) {
        job = tclService.initJobConfiguration(job);

        Optional<Flow> flow = Optional.ofNullable(tclService.getNextFlow(job));
        if (flow.isPresent()) {
            log.info("Execute command: {} \n {}", flow.get().getType(), flow.get().getCommands());
            String stepId = tclService.getCurrentStepId(job);
            FlowType tempFlowType = FlowType.valueOf(flow.get().getType());

            switch (tempFlowType) {
                case terraformPlan:
                case terraformApply:
                case terraformDestroy:
                case customScripts:
                    if (executorService.execute(job, stepId, flow.get()))
                        log.info("Executing Job {} Step Id {}", job.getId(), stepId);
                    else {
                        log.error("Error when sending context to executor marking job {} as failed", job.getId());
                        job.setStatus(JobStatus.failed);
                        jobRepository.save(job);
                    }
                    break;
                case approval:
                    job.setStatus(JobStatus.waitingApproval);
                    job.setApprovalTeam(flow.get().getTeam());
                    jobRepository.save(job);
                    log.info("Waiting Approval for Job {} Step Id {}", job.getId(), stepId);
                    break;
                default:
                    log.error("FlowType not supported");
                    break;
            }
        } else {
            job.setStatus(JobStatus.completed);
            jobRepository.save(job);
            log.info("Update Job {} to completed", job.getId());
        }
    }

    private void executeApprovedJobs(Job job) {
        job = tclService.initJobConfiguration(job);
        Optional<Flow> flow = Optional.ofNullable(tclService.getNextFlow(job));
        if (flow.isPresent()) {
            log.info("Execute command: {} \n {}", flow.get().getType(), flow.get().getCommands());
            String stepId = tclService.getCurrentStepId(job);
            job.setApprovalTeam("");
            jobRepository.save(job);
            if (executorService.execute(job, stepId, flow.get()))
                log.info("Executing Job {} Step Id {}", job.getId(), stepId);
        }
    }

    private void cancelJobSteps(int jobId){
        for(Step step: stepRepository.findByJobId(jobId)){
            if(step.getStatus().equals(JobStatus.pending) || step.getStatus().equals(JobStatus.running)){
                step.setStatus(JobStatus.cancelled);
                stepRepository.save(step);
            }
        }
    }
}
