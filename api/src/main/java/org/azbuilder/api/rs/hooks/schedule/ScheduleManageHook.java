package org.azbuilder.api.rs.hooks.schedule;

import com.yahoo.elide.annotation.LifeCycleHookBinding;
import com.yahoo.elide.core.lifecycle.LifeCycleHook;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.scheduler.ScheduleWorkspaceService;
import org.azbuilder.api.rs.workspace.schedule.Schedule;
import org.quartz.SchedulerException;

import java.text.ParseException;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class ScheduleManageHook implements LifeCycleHook<Schedule> {

    private ScheduleWorkspaceService schedulerService;

    @Override
    public void execute(LifeCycleHookBinding.Operation operation, LifeCycleHookBinding.TransactionPhase transactionPhase, Schedule schedule, RequestScope requestScope, Optional<ChangeSpec> optional) {
        log.info("TriggerCreateHook {} {}", schedule.getCron(), schedule.getId().toString());
        try {
            switch (operation) {
                case CREATE:
                    schedulerService.createTask(schedule.getCron(), schedule.getId().toString());
                    break;
                case UPDATE:
                    if (!schedule.isEnabled()) {
                        schedulerService.deleteTask(schedule.getId().toString());
                    }
                    break;
                case DELETE:
                    schedulerService.deleteTask(schedule.getId().toString());
                    break;
                default:
                    log.info("Not supported");
                    break;
            }

        } catch (ParseException | SchedulerException e) {
            log.error(e.getMessage());
        }
    }
}
