package org.azbuilder.server.rs.job;

import com.yahoo.elide.annotation.Include;
import lombok.Getter;
import lombok.Setter;
import org.azbuilder.server.rs.Organization;
import org.azbuilder.server.rs.workspace.Workspace;

import javax.persistence.*;
import java.util.UUID;

@Include(type = "job")
@Getter
@Setter
@Entity
public class Job {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private Organization organization;

    @ManyToOne
    private Workspace workspace;
}

