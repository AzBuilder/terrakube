package org.azbuilder.api.plugin.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@AllArgsConstructor
@Slf4j
@Service
public class ScheduleWorkspaceService {

    private static final String PREFIX_JOB = "Terrakube_Trigger_";

    Scheduler scheduler;

    public void createTask(String cronExpression, String triggerId) throws ParseException, SchedulerException {
        if(jobExists(triggerId)) {
            deleteTask(triggerId);
            createQuartzJob(cronExpression, triggerId);
        }else{
            createQuartzJob(cronExpression, triggerId);
        }
    }

    private void createQuartzJob(String cronExpression, String triggerId) throws ParseException, SchedulerException {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduleJob.TRIGGER_ID, triggerId);


        JobDetail jobDetail = JobBuilder.newJob().ofType(ScheduleJob.class)
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

        log.info("Create Schedule Trigger {}", jobDetail.getKey());
        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void deleteTask(String triggerId) throws ParseException, SchedulerException {
        log.info("Delete Schedule Trigger {}", triggerId);
        scheduler.deleteJob(new JobKey(PREFIX_JOB + triggerId));
    }

    private boolean jobExists(String triggerId){
        boolean jobExists = false;
        try {
            scheduler.getJobDetail(new JobKey(PREFIX_JOB + triggerId));
            jobExists = true;
            log.info("JobId {} exists", triggerId);
        } catch (SchedulerException e) {
            log.error(e.getMessage());
        }
        return jobExists;
    }
}
