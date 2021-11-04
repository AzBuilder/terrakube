package org.azbuilder.api.plugin.scheduler;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@Slf4j
public class ScheduleJob implements Job {


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String message = jobExecutionContext.getJobDetail().getJobDataMap().getString("message");

        log.info("Execute {}", message);
    }
}
