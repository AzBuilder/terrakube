package org.azbuilder.api.plugin.scheduler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.repository.JobRepository;
import org.azbuilder.api.repository.ScheduleRepository;
import org.azbuilder.api.repository.TemplateRepository;
import org.azbuilder.api.rs.job.Job;
import org.azbuilder.api.rs.job.JobStatus;
import org.azbuilder.api.rs.template.Template;
import org.azbuilder.api.rs.workspace.schedule.Schedule;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@Component
@Getter
@Setter
@Slf4j
public class ScheduleJob implements org.quartz.Job {

    public static final String TRIGGER_ID = "triggerId";
    public static final String TRIGGER_TCL = "triggerTcl";

    ScheduleRepository scheduleRepository;
    JobRepository jobRepository;
    TemplateRepository templateRepository;

    @Transactional
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String triggerId = jobExecutionContext.getJobDetail().getJobDataMap().getString(TRIGGER_ID);
        Schedule schedule = scheduleRepository.getById(UUID.fromString(triggerId));

        log.info("Creating new job for triggerId: {}", triggerId);
        Job job = new Job();
        job.setWorkspace(schedule.getWorkspace());
        job.setOrganization(schedule.getWorkspace().getOrganization());
        if(schedule.getTemplateReference() != null){
            Template template = templateRepository.getById(UUID.fromString(schedule.getTemplateReference()));
            job.setTcl(template.getTcl());
            job.setTemplateReference(schedule.getTemplateReference());
        }else {
            job.setTcl(schedule.getTcl());
        }
        job.setStatus(JobStatus.pending);
        job.setCreatedBy("serviceAccount");
        job.setUpdatedBy("serviceAccount");
        Date triggerDate = new Date(System.currentTimeMillis());
        job.setCreatedDate(triggerDate);
        job.setUpdatedDate(triggerDate);

        job = jobRepository.save(job);
        log.info("New jobId: {}", job.getId());
    }
}
