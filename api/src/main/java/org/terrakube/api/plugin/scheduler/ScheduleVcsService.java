package org.terrakube.api.plugin.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@AllArgsConstructor
@Service
@Slf4j
public class ScheduleVcsService {

    public static final String PREFIX_JOB = "TerrakubeV2_Vcs_";

    Scheduler scheduler;

    public void createTask(String cronExpression, String vcsId) throws ParseException, SchedulerException {
        if(jobExists(vcsId)) {
            deleteTask(vcsId);
            createQuartzJob(cronExpression, vcsId);
        }else{
            createQuartzJob(cronExpression, vcsId);
        }
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

    public void deleteTask(String vcsId) throws ParseException, SchedulerException {
        log.info("Delete Vcs Trigger {}", vcsId);
        scheduler.deleteJob(new JobKey(PREFIX_JOB + vcsId));
    }

    private boolean jobExists(String vcsId){
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
