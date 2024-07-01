package org.terrakube.api.plugin.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.terrakube.api.repository.StepRepository;
import org.terrakube.api.repository.WorkspaceRepository;
import org.terrakube.api.rs.job.Job;
import org.terrakube.api.rs.job.JobStatus;
import org.terrakube.api.rs.job.step.Step;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terrakube.api.rs.workspace.Workspace;

import java.text.ParseException;
import java.util.UUID;

@AllArgsConstructor
@Slf4j
@Service
public class ScheduleJobService {

    public static final String PREFIX_JOB = "TerrakubeV2_Trigger_";
    public static final String PREFIX_JOB_CONTEXT = "TerrakubeV2_Job_";
    public static final String CRON_SCHEDULE = "0 * * ? * *"; //CHECK EVERY MINUTES

    Scheduler scheduler;

    StepRepository stepRepository;

    WorkspaceRepository workspaceRepository;

    public void createJobTrigger(String cronExpression, String triggerId) throws ParseException, SchedulerException {

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduleJobTrigger.TRIGGER_ID, triggerId);

        JobDetail jobDetail = JobBuilder.newJob().ofType(ScheduleJobTrigger.class)
                .storeDurably()
                .setJobData(jobDataMap)
                .withIdentity(PREFIX_JOB + triggerId)
                .withDescription(triggerId)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .startNow()
                .forJob(jobDetail)
                .withIdentity(PREFIX_JOB + triggerId)
                .withDescription(triggerId)
                .withSchedule(CronScheduleBuilder.cronSchedule(new CronExpression(cronExpression)))
                .build();

        log.info("Create Schedule Job Trigger {}", jobDetail.getKey());
        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void createJobContext(Job job) throws ParseException, SchedulerException {

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduleJob.JOB_ID, job.getId());

        JobDetail jobDetail = JobBuilder.newJob().ofType(ScheduleJob.class)
                .storeDurably()
                .setJobData(jobDataMap)
                .withIdentity(PREFIX_JOB_CONTEXT + job.getId())
                .withDescription(String.valueOf(job.getId()))
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .startNow()
                .forJob(jobDetail)
                .withIdentity(PREFIX_JOB_CONTEXT + job.getId())
                .withDescription(String.valueOf(job.getId()))
                .withSchedule(CronScheduleBuilder.cronSchedule(new CronExpression(CRON_SCHEDULE)))
                .build();

        log.info("Create Job Context {}", jobDetail.getKey());

        Workspace workspace = job.getWorkspace();
        workspaceRepository.save(workspace);
        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void createJobContextNow(Job job) throws SchedulerException {

        String random = UUID.randomUUID().toString();
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduleJob.JOB_ID, job.getId());
        jobDataMap.put("isTriggerFromStatusChange", "true");
        jobDataMap.put("identity", PREFIX_JOB_CONTEXT + job.getId() + "_" + random);

        JobDetail jobDetail = JobBuilder.newJob().ofType(ScheduleJob.class)
                .storeDurably()
                .setJobData(jobDataMap)
                .withIdentity(PREFIX_JOB_CONTEXT + job.getId() + "_" + random)
                .withDescription(String.valueOf(job.getId()))
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .startNow()
                .forJob(jobDetail)
                .withIdentity(PREFIX_JOB_CONTEXT + job.getId() + "_" + random)
                .withDescription(String.valueOf(job.getId()))
                .startNow()
                .build();

        log.info("Running Job Context Now: {}", job.getId());
        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void deleteJobTrigger(String triggerId) throws ParseException, SchedulerException {
        log.info("Delete Schedule Job Trigger {}", triggerId);
        scheduler.deleteJob(new JobKey(PREFIX_JOB + triggerId));
    }

    @Transactional
    public void deleteJobContext(int jobId) throws ParseException, SchedulerException {
        log.info("Delete Job Context {}", jobId);
        scheduler.deleteJob(new JobKey(PREFIX_JOB_CONTEXT + jobId));
        for(Step step: stepRepository.findByJobId(jobId)){
            if(step.getStatus().equals(JobStatus.pending) || step.getStatus().equals(JobStatus.running)){
                step.setStatus(JobStatus.cancelled);
                stepRepository.save(step);
            }
        }
    }

}