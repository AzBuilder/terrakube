package org.terrakube.api.plugin.scheduler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.terrakube.api.repository.JobRepository;
import org.terrakube.api.repository.ScheduleRepository;
import org.terrakube.api.repository.TemplateRepository;
import org.terrakube.api.rs.job.Job;
import org.terrakube.api.rs.job.JobStatus;
import org.terrakube.api.rs.job.JobVia;
import org.terrakube.api.rs.template.Template;
import org.terrakube.api.rs.workspace.schedule.Schedule;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@Component
@Getter
@Setter
@Slf4j
public class ScheduleJobTrigger implements org.quartz.Job {

    public static final String TRIGGER_ID = "triggerId";
    public static final String TRIGGER_TCL = "triggerTcl";

    ScheduleRepository scheduleRepository;
    JobRepository jobRepository;
    TemplateRepository templateRepository;
    ScheduleJobService scheduleJobService;

    @Transactional
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String triggerId = jobExecutionContext.getJobDetail().getJobDataMap().getString(TRIGGER_ID);
        Schedule schedule = scheduleRepository.getReferenceById(UUID.fromString(triggerId));

        if (!schedule.getWorkspace().isLocked()) {
            log.info("Creating new job for triggerId: {}", triggerId);
            Job job = new Job();
            job.setRefresh(true);
            job.setRefreshOnly(false);
            job.setWorkspace(schedule.getWorkspace());
            job.setOrganization(schedule.getWorkspace().getOrganization());
            if (schedule.getTemplateReference() != null) {
                Template template = templateRepository.getReferenceById(UUID.fromString(schedule.getTemplateReference()));
                job.setTcl(template.getTcl());
                job.setTemplateReference(schedule.getTemplateReference());
            } else {
                job.setTcl(schedule.getTcl());
            }
            job.setStatus(JobStatus.pending);
            job.setCreatedBy("serviceAccount");
            job.setUpdatedBy("serviceAccount");
            job.setVia(JobVia.Schedule.name());
            Date triggerDate = new Date(System.currentTimeMillis());
            job.setCreatedDate(triggerDate);
            job.setUpdatedDate(triggerDate);

            job = jobRepository.save(job);
            log.info("New jobId: {}", job.getId());
            try {
                log.info("Creating Job Context: {}", job.getId());
                scheduleJobService.createJobContext(job);
            } catch (ParseException e) {
                log.error(e.getMessage());
            } catch (SchedulerException e) {
                log.error(e.getMessage());
            }
        } else {
            log.warn("Workspace {} {} is locked, new jobs can not be created until the lock is released", schedule.getWorkspace().getId(), schedule.getWorkspace().getName());
        }
    }

}
