package org.azbuilder.server.rs.job;

import com.yahoo.elide.annotation.Include;
import lombok.Getter;
import lombok.Setter;
import org.azbuilder.server.rs.Organization;
import org.azbuilder.server.rs.workspace.Workspace;

import javax.persistence.*;

@Include(type = "job")
@Getter
@Setter
@Entity
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    private Command command;

    private Status status;

    @ManyToOne
    private Organization organization;

    @ManyToOne
    private Workspace workspace;
}

enum Status{
    pending,
    queue,
    running,
    completed
}

enum Command{
    plan,
    apply,
    destroy
}

