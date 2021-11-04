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

    Scheduler scheduler;

    public void createTask(String cronExpression, String triggerId) throws ParseException, SchedulerException {

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduleJob.TRIGGER_ID, triggerId);


        JobDetail jobDetail = JobBuilder.newJob().ofType(ScheduleJob.class)
                .storeDurably()
                .setJobData(jobDataMap)
                .withIdentity("Terrakube_Trigger_" + triggerId)
                .withDescription(triggerId)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .startNow()
                .forJob(jobDetail)
                .withIdentity("Terrakube_Trigger_" + triggerId)
                .withDescription(triggerId)
                .withSchedule(CronScheduleBuilder.cronSchedule(new CronExpression(cronExpression)))
                .build();

        log.info("Create Schedule Trigger {}", jobDetail.getKey());
        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void deleteTask(String triggerId) throws ParseException, SchedulerException {
        log.info("Delete Schedule Trigger {}", triggerId);
        scheduler.deleteJob(new JobKey("Terrakube_Trigger_" + triggerId));
    }
}
