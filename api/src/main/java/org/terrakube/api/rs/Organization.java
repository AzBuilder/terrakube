package org.terrakube.api.rs;

import com.yahoo.elide.annotation.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.terrakube.api.rs.globalvar.Globalvar;
import org.terrakube.api.rs.hooks.organization.OrganizationManageHook;
import org.terrakube.api.rs.job.Job;
import org.terrakube.api.rs.module.Module;
import org.terrakube.api.rs.provider.Provider;
import org.terrakube.api.rs.ssh.Ssh;
import org.terrakube.api.rs.team.Team;
import org.terrakube.api.rs.template.Template;
import org.terrakube.api.rs.vcs.Vcs;
import org.terrakube.api.rs.workspace.Workspace;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@ReadPermission(expression = "user belongs organization")
@CreatePermission(expression = "user is a superuser")
@UpdatePermission(expression = "user is a superuser")
@DeletePermission(expression = "user is a superuser")
@LifeCycleHookBinding(operation = LifeCycleHookBinding.Operation.CREATE, phase = LifeCycleHookBinding.TransactionPhase.POSTCOMMIT, hook = OrganizationManageHook.class)
@LifeCycleHookBinding(operation = LifeCycleHookBinding.Operation.UPDATE, phase = LifeCycleHookBinding.TransactionPhase.PRECOMMIT, hook = OrganizationManageHook.class)
@Include
@Getter
@Setter
@Entity
@Where(clause = "disabled = false")
public class Organization {

    @Id
    @Type(type="uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "disabled")
    private boolean disabled;

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

    @UpdatePermission(expression = "user belongs organization")
    @OneToMany(mappedBy = "organization")
    private List<Vcs> vcs;

    @UpdatePermission(expression = "user belongs organization")
    @OneToMany(mappedBy = "organization")
    private List<Ssh> ssh;

    @UpdatePermission(expression = "user belongs organization")
    @OneToMany(mappedBy = "organization")
    private List<Template> template;

    @OneToMany(mappedBy = "organization")
    private List<Globalvar> globalvar;
}
