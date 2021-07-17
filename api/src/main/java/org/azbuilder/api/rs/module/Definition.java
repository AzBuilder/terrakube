package org.azbuilder.api.rs.module;


import com.yahoo.elide.annotation.Include;
import com.yahoo.elide.annotation.LifeCycleHookBinding;
import lombok.Getter;
import lombok.Setter;
import org.azbuilder.api.rs.hook.definition.CreateHook;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

import static com.yahoo.elide.annotation.LifeCycleHookBinding.Operation.CREATE;
import static com.yahoo.elide.annotation.LifeCycleHookBinding.TransactionPhase.PRECOMMIT;

@Include
@Getter
@Setter
@Entity
@LifeCycleHookBinding(operation = CREATE, phase = PRECOMMIT, hook = CreateHook.class)
public class Definition {

    @Id
    @Type(type="uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name="terraform_version")
    private String terraformVersion;

    @Column(name="version")
    private String version;

    @ManyToOne
    private Module module;

    @OneToMany(mappedBy = "definition")
    private List<Parameter> parameter;
}

enum Status {
    PRE_ALPHA,
    ALPHA,
    BETA,
    RELEASE_CANDIDATE,
    RELEASE_TO_MANUFACTURING,
    GENERAL_AVAILABILITY
}

