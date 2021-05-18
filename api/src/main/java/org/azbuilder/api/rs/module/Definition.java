package org.azbuilder.api.rs.module;


import com.yahoo.elide.annotation.Include;
import com.yahoo.elide.annotation.SharePermission;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Include(type = "definition")
@Getter
@Setter
@Entity
@SharePermission
public class Definition {

    @Id
    @GeneratedValue
    private UUID id;

    private Status status;

    private String source;

    private String sourceSample;

    private String terraformVersion;

    @ManyToOne
    private Module module;

    @OneToMany(mappedBy = "definition")
    private List<Parameter> parameter;
}

enum Status{
    preAlpha,
    alpha,
    beta,
    releaseCandidate,
    releaseToManufacturing,
    generalAvailability
}
