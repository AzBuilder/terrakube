package org.azbuilder.api.rs;

import com.yahoo.elide.annotation.Include;
import lombok.Getter;
import lombok.Setter;
import org.azbuilder.api.rs.job.Job;
import org.azbuilder.api.rs.module.Module;
import org.azbuilder.api.rs.workspace.Workspace;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Include
@Getter
@Setter
@Entity
public class Organization {

    @Id
    @Type(type="uuid-char")
    @GeneratedValue
    private UUID id;

    private String name;

    private String description;

    @OneToMany(mappedBy = "organization")
    private List<Workspace> workspace = new ArrayList<>();

    @OneToMany(mappedBy = "organization")
    private List<Module> module = new ArrayList<>();

    @OneToMany(mappedBy = "organization")
    private List<Job> job = new ArrayList<>();
}
