package org.terrakube.api.plugin.scheduler.startup;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
public class ModuleStartupJobService {

    private static final String PREFIX_JOB_MODULE_REFRESH = "TerrakubeV2_ModuleRefresh";
    private static final String CRON_SCHEDULE = "0 */3 * ? * *"; //CHECK 3 MINUTES

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
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

    }

    public void runModuleRefresher() throws SchedulerException {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("ModuleRefresh", "ModuleRefreshV1");

        JobDetail jobDetail = JobBuilder.newJob().ofType(ModuleStartupJob.class)
                .storeDurably()
                .setJobData(jobDataMap)
                .withIdentity(PREFIX_JOB_MODULE_REFRESH + "_" + UUID.randomUUID())
                .withDescription("ModuleRefreshStartup")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .startNow()
                .forJob(jobDetail)
                .withIdentity(PREFIX_JOB_MODULE_REFRESH)
                .withDescription("ModuleRefreshV1")
                .startNow()
                .build();

        log.info("Create Schedule Module Refresh: {}", jobDetail.getKey());
        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void setupModuleRefreshJob() throws ParseException, SchedulerException {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("ModuleRefresh", "ModuleRefreshV1");

        JobDetail jobDetail = JobBuilder.newJob().ofType(ModuleStartupJob.class)
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
                .withSchedule(CronScheduleBuilder.cronSchedule(new CronExpression(CRON_SCHEDULE)))
                .build();

        log.info("Create Schedule Job Trigger {}", jobDetail.getKey());
        scheduler.scheduleJob(jobDetail, trigger);
    }
}
