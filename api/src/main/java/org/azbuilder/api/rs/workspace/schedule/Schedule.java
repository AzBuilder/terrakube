package org.azbuilder.api.rs.workspace.schedule;

import com.yahoo.elide.annotation.Include;
import com.yahoo.elide.annotation.LifeCycleHookBinding;
import lombok.Getter;
import lombok.Setter;
import org.azbuilder.api.plugin.security.audit.GenericAuditFields;
import org.azbuilder.api.rs.hooks.schedule.ScheduleManageHook;
import org.azbuilder.api.rs.workspace.Workspace;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@LifeCycleHookBinding(operation = LifeCycleHookBinding.Operation.CREATE, phase = LifeCycleHookBinding.TransactionPhase.POSTCOMMIT, hook = ScheduleManageHook.class)
@LifeCycleHookBinding(operation = LifeCycleHookBinding.Operation.UPDATE, phase = LifeCycleHookBinding.TransactionPhase.POSTCOMMIT, hook = ScheduleManageHook.class)
@LifeCycleHookBinding(operation = LifeCycleHookBinding.Operation.DELETE, phase = LifeCycleHookBinding.TransactionPhase.POSTCOMMIT, hook = ScheduleManageHook.class)
@Include(rootLevel = false)
@Getter
@Setter
@Entity
public class Schedule extends GenericAuditFields {

    @Id
    @Type(type="uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name = "cron")
    private String cron;

    @Column(name = "tcl")
    private String tcl;

    @Column(name = "description")
    private String description;

    @Column(name = "enabled")
    private boolean enabled = true;

    @ManyToOne
    private Workspace workspace;
}
