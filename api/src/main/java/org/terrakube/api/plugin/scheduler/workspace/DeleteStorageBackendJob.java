package org.terrakube.api.plugin.scheduler.workspace;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import org.terrakube.api.plugin.storage.StorageTypeService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Component
public class DeleteStorageBackendJob implements Job {

    StorageTypeService storageTypeService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String workspaceId = jobExecutionContext.getJobDetail().getJobDataMap().getString("workspaceId");
        String organizationId = jobExecutionContext.getJobDetail().getJobDataMap().getString("organizationId");
        List<Integer> jobList = (List<Integer>) jobExecutionContext.getJobDetail().getJobDataMap().get("jobList");

        storageTypeService.deleteWorkspaceOutputData(organizationId, jobList);
        storageTypeService.deleteWorkspaceStateData(organizationId, workspaceId);
    }
}
