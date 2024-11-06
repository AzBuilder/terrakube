package org.terrakube.api.rs.collection;

import com.yahoo.elide.annotation.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.terrakube.api.plugin.security.audit.GenericAuditFields;
import org.terrakube.api.rs.IdConverter;
import org.terrakube.api.rs.Organization;

import java.sql.Types;
import java.util.UUID;

//@ReadPermission(expression = "team view workspace")
//@CreatePermission(expression = "team manage workspace")
//@UpdatePermission(expression = "team manage workspace")
//@DeletePermission(expression = "team manage workspace")
//@LifeCycleHookBinding(operation = LifeCycleHookBinding.Operation.UPDATE, phase = LifeCycleHookBinding.TransactionPhase.PRECOMMIT, hook = WorkspaceManageHook.class)
//@LifeCycleHookBinding(operation = LifeCycleHookBinding.Operation.CREATE, phase = LifeCycleHookBinding.TransactionPhase.PRECOMMIT, hook = WorkspaceManageHook.class)
@Include
@Getter
@Setter
@Entity(name = "collection")
@SQLRestriction(value = "deleted = false")
public class Collection extends GenericAuditFields {

    @Id
    @JdbcTypeCode(Types.VARCHAR)
    @Convert(converter = IdConverter.class)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "priority")
    private int priority;

    @ManyToOne
    private Organization organization;
}
