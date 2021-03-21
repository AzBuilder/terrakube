package org.azbuilder.server.model;

import com.yahoo.elide.annotation.Include;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.sql.Timestamp;
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
    private List<Variable> variables;

    @OneToMany(mappedBy = "workspace")
    private List<Secret> secrets;

    @OneToMany(mappedBy = "workspace")
    private List<Job> jobs;

    @OneToOne
    private Module module;
}
