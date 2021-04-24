package org.azbuilder.server.rs.workspace;

import com.yahoo.elide.annotation.Include;
import com.yahoo.elide.annotation.SharePermission;
import lombok.Getter;
import lombok.Setter;
import org.azbuilder.server.rs.*;
import org.azbuilder.server.rs.job.Job;
import org.azbuilder.server.rs.module.Definition;
import org.azbuilder.server.rs.workspace.parameters.Environment;
import org.azbuilder.server.rs.workspace.parameters.Secret;
import org.azbuilder.server.rs.workspace.parameters.Variable;

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
