package org.azbuilder.api.rs.workspace;

import com.yahoo.elide.annotation.Include;
import com.yahoo.elide.annotation.SharePermission;
import lombok.Getter;
import lombok.Setter;
import org.azbuilder.api.rs.Organization;
import org.azbuilder.api.rs.workspace.parameters.Secret;
import org.azbuilder.api.rs.workspace.parameters.Variable;
import org.azbuilder.api.rs.job.Job;
import org.azbuilder.api.rs.module.Definition;
import org.azbuilder.api.rs.workspace.parameters.Environment;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Include(type = "workspace")
@Getter
@Setter
@Entity
@SharePermission
public class Workspace {

    @Id
    @Type(type="uuid-char")
    @GeneratedValue
    private UUID id;

    private String name;

    @ManyToOne
    private Organization organization;

    @OneToMany(mappedBy = "workspace")
    private List<Variable> variable;

    @OneToMany(mappedBy = "workspace")
    private List<Secret> secret;

    @OneToMany(mappedBy = "workspace")
    private List<Environment> environment;

    @OneToOne
    private Definition definition;

    @OneToMany(mappedBy = "workspace")
    private List<Job> job;
}
