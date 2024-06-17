package org.terrakube.api.rs.hooks.job;

import com.yahoo.elide.annotation.LifeCycleHookBinding;
import com.yahoo.elide.core.lifecycle.LifeCycleHook;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.terrakube.api.plugin.scheduler.ScheduleJobService;
import org.terrakube.api.rs.job.Job;
import org.terrakube.api.rs.job.JobStatus;
import org.quartz.SchedulerException;

import java.text.ParseException;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class JobManageHook implements LifeCycleHook<Job> {

    private ScheduleJobService scheduleJobService;

    @Override
    public void execute(LifeCycleHookBinding.Operation operation, LifeCycleHookBinding.TransactionPhase transactionPhase, Job job, RequestScope requestScope, Optional<ChangeSpec> optional) {
        log.info("JobCreateHook {}", job.getId());
        try {
            switch (operation){
                case CREATE:
                    scheduleJobService.createJobContext(job);
                    break;
                case UPDATE:
                    if(job.getStatus().equals(JobStatus.cancelled)) {
                        scheduleJobService.deleteJobContext(job.getId());
                    }
                    break;
                default:
                    log.info("Not supported {}", operation);
                    break;
            }

        } catch (ParseException | SchedulerException e) {
            log.error(e.getMessage());
        }
    }
}
