package org.terrakube.api.rs;

import java.sql.Types;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Where;
import org.terrakube.api.rs.agent.Agent;
import org.terrakube.api.rs.globalvar.Globalvar;
import org.terrakube.api.rs.hooks.organization.OrganizationManageHook;
import org.terrakube.api.rs.job.Job;
import org.terrakube.api.rs.module.Module;
import org.terrakube.api.rs.provider.Provider;
import org.terrakube.api.rs.ssh.Ssh;
import org.terrakube.api.rs.tag.Tag;
import org.terrakube.api.rs.team.Team;
import org.terrakube.api.rs.template.Template;
import org.terrakube.api.rs.vcs.Vcs;
import org.terrakube.api.rs.workspace.Workspace;

import com.yahoo.elide.annotation.CreatePermission;
import com.yahoo.elide.annotation.DeletePermission;
import com.yahoo.elide.annotation.Include;
import com.yahoo.elide.annotation.LifeCycleHookBinding;
import com.yahoo.elide.annotation.ReadPermission;
import com.yahoo.elide.annotation.UpdatePermission;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@ReadPermission(expression = "user belongs organization")
@CreatePermission(expression = "user is a superuser")
@UpdatePermission(expression = "user is a superuser")
@DeletePermission(expression = "user is a superuser")
@LifeCycleHookBinding(operation = LifeCycleHookBinding.Operation.CREATE, phase = LifeCycleHookBinding.TransactionPhase.POSTCOMMIT, hook = OrganizationManageHook.class)
@LifeCycleHookBinding(operation = LifeCycleHookBinding.Operation.UPDATE, phase = LifeCycleHookBinding.TransactionPhase.PRECOMMIT, hook = OrganizationManageHook.class)
@Include
@Getter
@Setter
@Entity(name = "organization")
@Where(clause = "disabled = false")
public class Organization {

    @Id
    @JdbcTypeCode(Types.VARCHAR)
    @Convert(converter = IdConverter.class)
    @GeneratedValue(strategy = GenerationType.UUID)
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
    private List<Agent> agent;

    @UpdatePermission(expression = "user belongs organization")
    @OneToMany(mappedBy = "organization")
    private List<Template> template;

    @OneToMany(mappedBy = "organization")
    private List<Globalvar> globalvar;

    @OneToMany(mappedBy = "organization")
    private List<Tag> tag;

    @Column(name = "execution_mode")
    private String executionMode;
}
