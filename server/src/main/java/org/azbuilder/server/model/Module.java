package org.azbuilder.server.model;


import com.yahoo.elide.annotation.Include;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Include(type = "module")
@Getter
@Setter
@Entity
public class Module {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String description;
    private String source;

    @ManyToOne
    private Organization organization;

    @OneToMany(mappedBy = "module")
    private List<Workspace> workspaces;
}
