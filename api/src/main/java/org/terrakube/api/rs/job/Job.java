package org.terrakube.api.rs.job;

import com.yahoo.elide.annotation.*;
import lombok.Getter;
import lombok.Setter;
import org.terrakube.api.plugin.security.audit.GenericAuditFields;
import org.terrakube.api.rs.IdConverter;
import org.terrakube.api.rs.Organization;
import org.terrakube.api.rs.hooks.job.JobManageHook;
import org.terrakube.api.rs.job.step.Step;
import org.terrakube.api.rs.workspace.Workspace;

import jakarta.persistence.*;
import java.util.List;

@LifeCycleHookBinding(operation = LifeCycleHookBinding.Operation.CREATE, phase = LifeCycleHookBinding.TransactionPhase.POSTCOMMIT, hook = JobManageHook.class)
@LifeCycleHookBinding(operation = LifeCycleHookBinding.Operation.UPDATE, phase = LifeCycleHookBinding.TransactionPhase.POSTCOMMIT, hook = JobManageHook.class)
@ReadPermission(expression = "team view job")
@CreatePermission(expression = "team manage job")
@UpdatePermission(expression = "team manage job OR user is a super service")
@Include(rootLevel = false)
@Getter
@Setter
@Entity(name = "job")
public class Job extends GenericAuditFields {

    @Id
    @Convert(converter = IdConverter.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "comments")
    private String comments;

    @UpdatePermission(expression = "team approve job OR user is a super service")
    @Enumerated(EnumType.STRING)
    private JobStatus status = JobStatus.pending;

    @Column(name = "output")
    private String output;

    @Column(name = "commit_id")
    private String commitId;

    @Column(name = "terraform_plan")
    private String terraformPlan;

    @CreatePermission(expression = "user is a super service")
    @UpdatePermission(expression = "user is a super service")
    @Column(name = "approval_team")
    private String approvalTeam;

    @Column(name = "tcl")
    private String tcl;

    @Exclude
    @Column(name = "override_source")
    private String overrideSource;

    @Exclude
    @Column(name = "override_branch")
    private String overrideBranch;

    @Column(name = "template_reference")
    private String templateReference;

    @Column(name = "via")
    private String via = "UI";

    @Column(name = "refresh")
    private boolean refresh = true;

    @Column(name = "refresh_only")
    private boolean refreshOnly = false;

    @ManyToOne
    private Organization organization;

    @ManyToOne
    private Workspace workspace;

    @UpdatePermission(expression = "user is a super service")
    @OneToMany(mappedBy = "job")
    private List<Step> step;

}

