package org.terrakube.api.plugin.scheduler;

import java.text.ParseException;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Service
@Slf4j
public class ScheduleVcsService {

    public static final String PREFIX_JOB = "TerrakubeV2_Vcs_";

    Scheduler scheduler;

    public void createTask(String cronExpression, String vcsId) throws ParseException, SchedulerException {
        if (jobExists(vcsId)) {
            deleteTask(vcsId);
            createQuartzJob(cronExpression, vcsId);
        } else {
            createQuartzJob(cronExpression, vcsId);
        }
    }

    // Fire an one-off job
    public void createTask(String vcsId) throws ParseException, SchedulerException {
        createQuartzJob(vcsId);
    }

    private void createQuartzJob(String cronExpression, String vcsId) throws SchedulerException, ParseException {
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

    private void createQuartzJob(String vcsId) throws SchedulerException, ParseException {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduleVcs.VCS_ID, vcsId);

        scheduler.getJobGroupNames();

        JobDetail jobDetail = JobBuilder.newJob().ofType(ScheduleVcs.class)
                .storeDurably()
                .setJobData(jobDataMap)
                .withIdentity(PREFIX_JOB + vcsId)
                .withDescription(vcsId)
                .build();

        scheduler.addJob(jobDetail, true);
        log.info("Triggering VCS Job {} on VCS {}", jobDetail.getKey(), vcsId);
        scheduler.triggerJob(new JobKey(PREFIX_JOB + vcsId));
    }

    public void deleteTask(String vcsId) throws ParseException, SchedulerException {
        log.info("Delete Vcs Trigger {}", vcsId);
        scheduler.deleteJob(new JobKey(PREFIX_JOB + vcsId));
    }

    private boolean jobExists(String vcsId) {
        boolean jobExists = false;
        try {
            scheduler.getJobDetail(new JobKey(PREFIX_JOB + vcsId));
            jobExists = true;
            log.info("JobId {} exists", vcsId);
        } catch (SchedulerException e) {
            log.error(e.getMessage());
        }
        return jobExists;
    }
}
