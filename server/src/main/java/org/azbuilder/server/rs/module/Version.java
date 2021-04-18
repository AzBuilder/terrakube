package org.azbuilder.server.rs.module;


import com.yahoo.elide.annotation.Include;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Include(type = "version")
@Getter
@Setter
@Entity
public class Version {
    @Id
    private String id;

    private Status status;

    private String source;

    private String sourceApply;

    @ManyToOne
    private Module module;

    @OneToMany(mappedBy = "version")
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
