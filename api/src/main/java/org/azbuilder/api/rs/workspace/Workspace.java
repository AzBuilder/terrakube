package org.azbuilder.api.rs.workspace;

import com.yahoo.elide.annotation.Include;
import lombok.Getter;
import lombok.Setter;
import org.azbuilder.api.rs.Organization;
import org.azbuilder.api.rs.workspace.parameters.Variable;
import org.azbuilder.api.rs.job.Job;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Include
@Getter
@Setter
@Entity
public class Workspace {

    @Id
    @Type(type="uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name = "owner")
    private String owner;

    @Column(name = "name")
    private String name;

    @Column(name = "source")
    private String source;

    @Column(name = "branch")
    private String branch;

    @Column(name = "terraform_version")
    private String terraformVersion;

    @ManyToOne
    private Organization organization;

    @OneToMany(mappedBy = "workspace")
    private List<Variable> variable;

    @OneToMany(mappedBy = "workspace")
    private List<Job> job;
}
