package org.azbuilder.api.rs.job;

import com.yahoo.elide.annotation.Include;
import lombok.Getter;
import lombok.Setter;
import org.azbuilder.api.rs.Organization;
import org.azbuilder.api.rs.workspace.Workspace;

import javax.persistence.*;

@Include(type = "job")
@Getter
@Setter
@Entity
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Command command;

    private Status status = Status.pending;

    @Lob
    private String output;

    @ManyToOne
    private Organization organization;

    @ManyToOne
    private Workspace workspace;

}

enum Command{
    plan,
    apply,
    destroy
}

