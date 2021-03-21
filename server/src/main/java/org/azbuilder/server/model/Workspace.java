package org.azbuilder.server.model;

import com.yahoo.elide.annotation.Include;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Include(type = "workspace")
@Getter
@Setter
@Entity
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
    private List<Job> job;

    @OneToOne
    private Module module;
}
