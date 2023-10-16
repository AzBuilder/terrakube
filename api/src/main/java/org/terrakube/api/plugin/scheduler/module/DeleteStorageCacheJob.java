package org.terrakube.api.plugin.scheduler.module;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import org.terrakube.api.plugin.storage.StorageTypeService;

@Slf4j
@AllArgsConstructor
@Component
public class DeleteStorageCacheJob implements Job {

    StorageTypeService storageTypeService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Deleting module storage backened data....");
        storageTypeService.deleteModuleStorage(
                jobExecutionContext.getJobDetail().getJobDataMap().getString("moduleOrganization"),
                jobExecutionContext.getJobDetail().getJobDataMap().getString("moduleName"),
                jobExecutionContext.getJobDetail().getJobDataMap().getString("moduleProvider"));
    }
}
