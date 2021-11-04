package org.azbuilder.api.rs.workspace.trigger;

import com.yahoo.elide.annotation.Include;
import com.yahoo.elide.annotation.LifeCycleHookBinding;
import lombok.Getter;
import lombok.Setter;
import org.azbuilder.api.plugin.security.audit.GenericAuditFields;
import org.azbuilder.api.rs.hooks.trigger.TriggerCreateHook;
import org.azbuilder.api.rs.workspace.Workspace;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@LifeCycleHookBinding(operation = LifeCycleHookBinding.Operation.CREATE, phase = LifeCycleHookBinding.TransactionPhase.POSTCOMMIT, hook = TriggerCreateHook.class)
@Include(rootLevel = false)
@Getter
@Setter
@Entity
public class Trigger extends GenericAuditFields {

    @Id
    @Type(type="uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name = "cron")
    private String cron;

    @Column(name = "tcl")
    private String tcl;

    @ManyToOne
    private Workspace workspace;
}
