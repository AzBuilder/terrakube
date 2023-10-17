package org.terrakube.api.plugin.softdelete;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.terrakube.api.plugin.scheduler.ScheduleJobService;
import org.terrakube.api.plugin.scheduler.module.DeleteStorageCacheJob;
import org.terrakube.api.repository.ScheduleRepository;
import org.terrakube.api.repository.WorkspaceRepository;
import org.terrakube.api.rs.Organization;
import org.terrakube.api.rs.job.Job;
import org.terrakube.api.rs.workspace.Workspace;
import org.terrakube.api.rs.workspace.schedule.Schedule;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Slf4j
@Service
public class SoftDeleteService {

    private static final String PREFIX_JOB_MODULE_DELETE_STORAGE="TerrakubeV2_WorkspaceDeleteStorage";

    ScheduleJobService scheduleJobService;

    ScheduleRepository scheduleRepository;

    WorkspaceRepository workspaceRepository;

    Scheduler scheduler;

    public void disableWorkspaceSchedules(Workspace workspace){
        for(Schedule schedule: workspace.getSchedule()){
            try {
                scheduleJobService.deleteJobTrigger(schedule.getId().toString());
                schedule.setEnabled(false);
                scheduleRepository.save(schedule);
            } catch (ParseException | SchedulerException e) {
                log.error(e.getMessage());
            }
        }

        deleteWorkspaceStorage(workspace);
    }

    public void deleteWorkspaceStorage(Workspace workspace){
        List<Job> jobList = workspace.getJob();
        List<Integer> jobIdList = new ArrayList();
        jobList.forEach(job -> jobIdList.add(job.getId()));
        String workspaceId = workspace.getId().toString();
        String organizationId = workspace.getOrganization().getId().toString();

        try {
            log.info("Setup job to delete storage for organization {} workspace {} jobs {}", organizationId, workspaceId, jobIdList);
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("organizationId", organizationId);
            jobDataMap.put("workspaceId", workspaceId);
            jobDataMap.put("jobList", jobIdList);

            JobDetail jobDetail = JobBuilder.newJob().ofType(DeleteStorageCacheJob.class)
                    .storeDurably()
                    .setJobData(jobDataMap)
                    .withIdentity(PREFIX_JOB_MODULE_DELETE_STORAGE + "_" + UUID.randomUUID())
                    .withDescription("WorkspaceDeleteStorage")
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .startNow()
                    .forJob(jobDetail)
                    .withIdentity(PREFIX_JOB_MODULE_DELETE_STORAGE + "_" + UUID.randomUUID())
                    .withDescription("WorkspaceDeleteStorageV1")
                    .startNow()
                    .build();

            log.info("Creating schedule to delete workspace data: {}", jobDetail.getKey());
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error(e.getMessage());
        }
    }

    public void disableOrganization(Organization organization){
        log.info("Disable Organization Id: {}", organization.getId().toString());
        for(Workspace workspace: organization.getWorkspace()){
            log.info("Disable Workspace: {}", workspace.getId().toString());
            disableWorkspaceSchedules(workspace);
            workspace.setDeleted(true);
            workspaceRepository.save(workspace);
        }

    }
}
