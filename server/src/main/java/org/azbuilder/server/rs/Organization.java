package org.azbuilder.server.rs;

import com.yahoo.elide.annotation.Include;
import lombok.Getter;
import lombok.Setter;
import org.azbuilder.server.rs.job.Job;
import org.azbuilder.server.rs.module.Module;
import org.azbuilder.server.rs.workspace.Workspace;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Include(rootLevel = true, type = "organization")
@Getter
@Setter
@Entity
@Table(name = "organization")
public class Organization {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @OneToMany(mappedBy = "organization")
    private List<Workspace> workspace;

    @OneToMany(mappedBy = "organization")
    private List<Module> module;

    @OneToMany(mappedBy = "organization")
    private List<Job> job;
}
