package org.azbuilder.api.rs.hooks.trigger;

import com.yahoo.elide.annotation.LifeCycleHookBinding;
import com.yahoo.elide.core.lifecycle.LifeCycleHook;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.scheduler.SchedulerServiceImpl;
import org.azbuilder.api.rs.workspace.trigger.Trigger;
import org.quartz.SchedulerException;

import java.text.ParseException;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class TriggerCreateHook implements LifeCycleHook<Trigger> {

    private SchedulerServiceImpl schedulerService;

    @Override
    public void execute(LifeCycleHookBinding.Operation operation, LifeCycleHookBinding.TransactionPhase transactionPhase, Trigger trigger, RequestScope requestScope, Optional<ChangeSpec> optional) {
        log.info("TriggerCreateHook {} {}", trigger.getCron(), trigger.getId().toString());
        try {
            schedulerService.task(trigger.getCron(), trigger.getId().toString());
            log.info("ya lo mande");
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
