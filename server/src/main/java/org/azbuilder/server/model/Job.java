package org.azbuilder.server.model;

import com.yahoo.elide.annotation.Include;
import lombok.Getter;
import lombok.Setter;

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

