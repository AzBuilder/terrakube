package org.azbuilder.api.plugin.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@AllArgsConstructor
@Service
@Slf4j
public class ScheduleVcsService {

    private static final String PREFIX_JOB = "Terrakube_Vcs_";

    Scheduler scheduler;

    public void createTask(String cronExpression, String vcsId) throws ParseException, SchedulerException {

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduleVcs.VCS_ID, vcsId);

        scheduler.getJobGroupNames();

        JobDetail jobDetail = JobBuilder.newJob().ofType(ScheduleVcs.class)
                .storeDurably()
                .setJobData(jobDataMap)
                .withIdentity(PREFIX_JOB + vcsId)
                .withDescription(vcsId)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .startNow()
                .forJob(jobDetail)
                .withIdentity(PREFIX_JOB + vcsId)
                .withDescription(vcsId)
                .withSchedule(CronScheduleBuilder.cronSchedule(new CronExpression(cronExpression)))
                .build();

        log.info("Create Vcs Trigger {}", jobDetail.getKey());
        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void deleteTask(String triggerId) throws ParseException, SchedulerException {
        log.info("Delete Vcs Trigger {}", triggerId);
        scheduler.deleteJob(new JobKey(PREFIX_JOB + triggerId));
    }
}
