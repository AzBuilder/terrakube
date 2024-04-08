package org.terrakube.api.plugin.scheduler.module;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
@Profile("!test")
public class CacheService {

    private static final String PREFIX_JOB_MODULE_REFRESH = "TerrakubeV2_ModuleRefresh";

    private CacheConfigurationProperties cacheConfigurationProperties;
    private Scheduler scheduler;

    @Transactional
    @PostConstruct
    public void initModuleStartup() throws ParseException, SchedulerException {
        try {
            log.info("Run module index scan");
            runModuleRefresher();
            log.info("Disable Old Module Refresh");
            JobDetail jobDetail = scheduler.getJobDetail(new JobKey(PREFIX_JOB_MODULE_REFRESH));
            log.info("jobDetail is null {}", jobDetail == null);
            if(jobDetail == null){
                setupModuleRefreshJob(cacheConfigurationProperties.getSchedule());
            } else {
                log.info("Delete Old Quartz Job");
                scheduler.deleteJob(new JobKey(PREFIX_JOB_MODULE_REFRESH));
                log.info("Reschedule with new frequency");
                setupModuleRefreshJob(cacheConfigurationProperties.getSchedule());
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

    }

    public void runModuleRefresher() throws SchedulerException {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("ModuleRefresh", "ModuleRefreshV1");

        JobDetail jobDetail = JobBuilder.newJob().ofType(CacheJob.class)
                .storeDurably()
                .setJobData(jobDataMap)
                .withIdentity(PREFIX_JOB_MODULE_REFRESH + "_" + UUID.randomUUID())
                .withDescription("ModuleRefreshStartup")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .startNow()
                .forJob(jobDetail)
                .withIdentity(PREFIX_JOB_MODULE_REFRESH+ "_" + UUID.randomUUID())
                .withDescription("ModuleRefreshV1")
                .startNow()
                .build();

        log.info("Create Schedule Module Refresh: {}", jobDetail.getKey());
        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void setupModuleRefreshJob(String quartzSchedule) throws ParseException, SchedulerException {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("ModuleRefresh", "ModuleRefreshV1");

        JobDetail jobDetail = JobBuilder.newJob().ofType(CacheJob.class)
                .storeDurably()
                .setJobData(jobDataMap)
                .withIdentity(PREFIX_JOB_MODULE_REFRESH)
                .withDescription("ModuleRefreshV1")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .startNow()
                .forJob(jobDetail)
                .withIdentity(PREFIX_JOB_MODULE_REFRESH)
                .withDescription("ModuleRefreshV1")
                .withSchedule(CronScheduleBuilder.cronSchedule(new CronExpression(quartzSchedule)))
                .build();

        log.info("Create Schedule Job Trigger {}", jobDetail.getKey());
        scheduler.scheduleJob(jobDetail, trigger);
    }
}
