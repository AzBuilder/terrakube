package org.terrakube.api.rs.workspace;

import com.yahoo.elide.annotation.*;
import lombok.Getter;
import lombok.Setter;
import org.terrakube.api.rs.Organization;
import org.terrakube.api.rs.ssh.Ssh;
import org.terrakube.api.rs.vcs.Vcs;
import org.terrakube.api.rs.workspace.parameters.Variable;
import org.terrakube.api.rs.job.Job;
import org.terrakube.api.rs.workspace.history.History;
import org.terrakube.api.rs.workspace.schedule.Schedule;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;


@ReadPermission(expression = "team view workspace")
@CreatePermission(expression = "team manage workspace")
@UpdatePermission(expression = "team manage workspace")
@DeletePermission(expression = "team manage workspace")
@Include
@Getter
@Setter
@Entity
@Where(clause = "deleted = false")
public class Workspace {

    @Id
    @Type(type="uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "source")
    private String source;

    @Column(name = "branch")
    private String branch;

    @Column(name = "folder")
    private String folder;

    @Column(name = "locked")
    private boolean locked;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "terraform_version")
    private String terraformVersion;

    @ManyToOne
    private Organization organization;

    @OneToMany(mappedBy = "workspace")
    private List<Variable> variable;

    @UpdatePermission(expression = "user is a super service")
    @OneToMany(mappedBy = "workspace")
    private List<History> history;

    @OneToMany(mappedBy = "workspace")
    private List<Schedule> schedule;

    @OneToMany(mappedBy = "workspace")
    private List<Job> job;

    @OneToOne
    private Vcs vcs;

    @OneToOne
    private Ssh ssh;
}
