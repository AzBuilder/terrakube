package org.terrakube.api.plugin.scheduler.migrate;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.terrakube.api.plugin.scheduler.ScheduleJobService;
import org.terrakube.api.plugin.scheduler.ScheduleVcsService;
import org.terrakube.api.plugin.vcs.TokenService;
import org.terrakube.api.repository.JobRepository;
import org.terrakube.api.repository.ScheduleRepository;
import org.terrakube.api.repository.VcsRepository;
import org.terrakube.api.rs.job.Job;
import org.terrakube.api.rs.vcs.Vcs;
import org.terrakube.api.rs.workspace.schedule.Schedule;

import java.text.ParseException;
import java.util.Calendar;
import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class MigrateJobs implements ApplicationListener<ContextRefreshedEvent> {

    private static final String DEPRECATED_PREFIX_JOB_VCS = "Terrakube_Vcs_";
    private static final String DEPRECATED_PREFIX_JOB_TRIGGER = "Terrakube_Trigger_";
    private static final String DEPRECATED_PREFIX_JOB_CONTEXT = "Terrakube_Job_";

    Scheduler scheduler;

    ScheduleVcsService scheduleVcsService;

    ScheduleJobService scheduleJobService;

    VcsRepository vcsRepository;

    ScheduleRepository scheduleRepository;

    JobRepository jobRepository;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            log.info("Migrating Quartz Jobs");
            for (String groupName : scheduler.getJobGroupNames()) {
                log.info("GroupName {}", groupName);
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    log.info("JobKey {} Class {}", jobKey.getName());

                    if (jobKey.getName().contains(DEPRECATED_PREFIX_JOB_VCS))
                        rescheduleVcs(jobKey.getName());

                    if (jobKey.getName().contains(DEPRECATED_PREFIX_JOB_TRIGGER))
                        rescheduleJobTrigger(jobKey.getName());

                    if (jobKey.getName().contains(DEPRECATED_PREFIX_JOB_CONTEXT))
                        rescheduleJob(jobKey.getName());
                }

            }
        } catch (SchedulerException e) {
            log.error(e.getMessage());
        }
    }

    public void rescheduleVcs(String jobKey) {
        log.info("Reschedule Vcs JobKey {}", jobKey);
        int minutes = Calendar.getInstance().get(Calendar.MINUTE);
        String vcsId = jobKey.replace(DEPRECATED_PREFIX_JOB_VCS, "");
        Vcs vcs = vcsRepository.getById(UUID.fromString(vcsId));
        try {
            switch (vcs.getVcsType()) {
                case GITHUB:
                    log.warn("GITHUB VCS does not require quartz job to refresh token");
                    break;
                case BITBUCKET:
                case GITLAB:
                    log.warn("VCS {} reschedule quartz VCS for {}", vcs.getVcsType(), jobKey);
                    scheduleVcsService.createTask(String.format(TokenService.QUARTZ_EVERY_60_MINUTES, minutes), vcsId);
                    scheduler.deleteJob(JobKey.jobKey(jobKey));
                    break;
                case AZURE_DEVOPS:
                    log.warn("VCS {} reschedule quartz VCS for {}", vcs.getVcsType(), jobKey);
                    scheduleVcsService.createTask(String.format(TokenService.QUARTZ_EVERY_30_MINUTES, minutes), vcsId);
                    scheduler.deleteJob(JobKey.jobKey(jobKey));
                    break;
                default:
                    log.warn("VCS Type not found to reschedule");
                    break;
            }
        } catch (ParseException | SchedulerException ex) {
            log.error(ex.getMessage());
        }
    }

    public void rescheduleJob(String jobKey) {
        log.info("Reschedule Job Context {}", jobKey);
        int jobId = Integer.parseInt(jobKey.replace(DEPRECATED_PREFIX_JOB_CONTEXT, ""));
        Job job = jobRepository.getById(jobId);
        try {
            scheduleJobService.createJobContext(job);
            scheduler.deleteJob(JobKey.jobKey(jobKey));
        } catch (ParseException | SchedulerException e) {
            log.error(e.getMessage());
        }
    }

    public void rescheduleJobTrigger(String jobKey) {
        log.info("Reschedule Job Trigger {}", jobKey);
        String scheduleId = jobKey.replace(DEPRECATED_PREFIX_JOB_TRIGGER, "");
        Schedule schedule = scheduleRepository.getById(UUID.fromString(scheduleId));
        try {
            scheduleJobService.createJobTrigger(schedule.getCron(), scheduleId);
            scheduler.deleteJob(JobKey.jobKey(jobKey));
        } catch (ParseException | SchedulerException e) {
            log.error(e.getMessage());
        }
    }
}
