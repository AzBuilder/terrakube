package org.azbuilder.api.rs.module;


import com.yahoo.elide.annotation.Include;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Include
@Getter
@Setter
@Entity
public class Definition {

    @Id
    @Type(type="uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name="source")
    private String source;

    @Column(name="source_sample")
    private String sourceSample;

    @Column(name="terraform_version")
    private String terraformVersion;

    @Enumerated(EnumType.STRING)
    DefinitionType type;

    @ManyToOne
    private Module module = null;

    @OneToMany(mappedBy = "definition")
    private List<Parameter> parameter;
}

enum DefinitionType{
    HTTP,
    GIT
}

enum Status{
    PRE_ALPHA,
    ALPHA,
    BETA,
    RELEASE_CANDIDATE,
    RELEASE_TO_MANUFACTURING,
    GENERAL_AVAILABILITY
}
