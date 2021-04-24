package org.azbuilder.server.rs.module;


import com.yahoo.elide.annotation.Include;
import com.yahoo.elide.annotation.SharePermission;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import org.azbuilder.server.rs.Organization;
import org.azbuilder.server.rs.workspace.Workspace;

import javax.persistence.*;
import java.util.List;

@Include(type = "definition")
@Getter
@Setter
@Entity
@SharePermission
public class Definition {
    @Id
    private String id;

    private Status status;

    private String source;

    private String sourceApply;

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
