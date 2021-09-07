package org.azbuilder.api.rs;

import com.yahoo.elide.annotation.*;
import lombok.Getter;
import lombok.Setter;
import org.azbuilder.api.rs.job.Job;
import org.azbuilder.api.rs.module.Module;
import org.azbuilder.api.rs.provider.Provider;
import org.azbuilder.api.rs.team.Team;
import org.azbuilder.api.rs.workspace.Workspace;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@ReadPermission(expression = "user belongs organization OR user is a service")
@CreatePermission(expression = "user is a superuser")
@UpdatePermission(expression = "user is a superuser ")
@DeletePermission(expression = "user is a superuser")
@Include
@Getter
@Setter
@Entity
public class Organization {

    @Id
    @Type(type="uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @UpdatePermission(expression = "user belongs organization")
    @OneToMany(mappedBy = "organization")
    private List<Workspace> workspace;

    @UpdatePermission(expression = "user belongs organization")
    @OneToMany(mappedBy = "organization")
    private List<Module> module;

    @UpdatePermission(expression = "user belongs organization")
    @OneToMany(mappedBy = "organization")
    private List<Provider> provider;

    @UpdatePermission(expression = "user belongs organization")
    @OneToMany(mappedBy = "organization")
    private List<Job> job;

    @OneToMany(mappedBy = "organization")
    private List<Team> team;
}
