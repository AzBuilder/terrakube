package org.terrakube.api.rs.hooks.module;

import com.yahoo.elide.annotation.LifeCycleHookBinding;
import com.yahoo.elide.core.lifecycle.LifeCycleHook;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.terrakube.api.plugin.scheduler.module.DeleteStorageCacheJob;
import org.terrakube.api.rs.module.Module;

import java.util.Optional;
import java.util.UUID;


@AllArgsConstructor
@Slf4j
public class ModuleManageHook implements LifeCycleHook<Module> {

    private static final String PREFIX_JOB_MODULE_DELETE_STORAGE = "TerrakubeV2_ModuleDeleteStorage";

    Scheduler scheduler;
    @Override
    public void execute(LifeCycleHookBinding.Operation operation, LifeCycleHookBinding.TransactionPhase transactionPhase, Module module, RequestScope requestScope, Optional<ChangeSpec> optional) {

        switch (operation) {
            case DELETE:
                try {
                    log.warn("ModuleManageHook Delete Storage for {}/{}/{}", module.getOrganization().getName(), module.getName(), module.getProvider());
                    JobDataMap jobDataMap = new JobDataMap();
                    jobDataMap.put("moduleOrganization", module.getOrganization().getName());
                    jobDataMap.put("moduleName", module.getName());
                    jobDataMap.put("moduleProvider", module.getProvider());

                    JobDetail jobDetail = JobBuilder.newJob().ofType(DeleteStorageCacheJob.class)
                            .storeDurably()
                            .setJobData(jobDataMap)
                            .withIdentity(PREFIX_JOB_MODULE_DELETE_STORAGE + "_" + UUID.randomUUID())
                            .withDescription("ModuleDeleteStorage")
                            .build();

                    Trigger trigger = TriggerBuilder.newTrigger()
                            .startNow()
                            .forJob(jobDetail)
                            .withIdentity(PREFIX_JOB_MODULE_DELETE_STORAGE + "_" + UUID.randomUUID())
                            .withDescription("ModuleDeleteStorageV1")
                            .startNow()
                            .build();

                    log.info("Create Schedule to delete module cache form storage: {}", jobDetail.getKey());
                    scheduler.scheduleJob(jobDetail, trigger);
                } catch (SchedulerException e) {
                    log.error(e.getMessage());
                }
                break;
            default:
                log.warn("Hook not supported in module");
                break;
        }
    }
}
