package org.azbuilder.api.plugin.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.UUID;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@AllArgsConstructor
@Slf4j
@Service
public class SchedulerServiceImpl {

    Scheduler scheduler;

    public void task(String cronExpression, String message) throws ParseException, SchedulerException {

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("message", message);

        JobDetail jobDetail = JobBuilder.newJob().ofType(ScheduleJob.class)
                .storeDurably()
                .setJobData(jobDataMap)
                .withIdentity("Qrtz_Job_Detail"+ UUID.randomUUID().toString())
                .withDescription("Invoke Sample Job service...")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .startNow()
                .forJob(jobDetail)
                .withIdentity("Qrtz_Trigger"+ UUID.randomUUID().toString())
                .withDescription("Sample trigger")
                .withSchedule(CronScheduleBuilder.cronSchedule(new CronExpression(cronExpression)))
                .build();

        log.info("Sheduler");
        scheduler.scheduleJob(jobDetail,trigger);
        log.info("Sheduler");
    }
}
