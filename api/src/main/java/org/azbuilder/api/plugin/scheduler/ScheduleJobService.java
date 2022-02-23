package org.azbuilder.api.plugin.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.rs.job.Job;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@AllArgsConstructor
@Slf4j
@Service
public class ScheduleJobService {

    public static final String PREFIX_JOB = "Terrakube_Trigger_";
    public static final String PREFIX_JOB_CONTEXT = "Terrakube_Job_";
    public static final String CRON_SCHEDULE = "0 * * ? * *"; //CHECK EVERY MINUTES

    Scheduler scheduler;

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
        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void deleteJobTrigger(String triggerId) throws ParseException, SchedulerException {
        log.info("Delete Schedule Job Trigger {}", triggerId);
        scheduler.deleteJob(new JobKey(PREFIX_JOB + triggerId));
    }

}