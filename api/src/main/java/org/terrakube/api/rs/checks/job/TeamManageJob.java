package org.terrakube.api.rs.checks.job;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.terrakube.api.plugin.security.groups.GroupService;
import org.terrakube.api.plugin.security.user.AuthenticatedUser;
import org.terrakube.api.rs.job.Job;
import org.terrakube.api.rs.team.Team;

import java.util.List;
import java.util.Optional;

@Slf4j
@SecurityCheck(TeamManageJob.RULE)
public class TeamManageJob extends OperationCheck<Job> {
    public static final String RULE = "team manage job";

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Autowired
    GroupService groupService;

    @Override
    public boolean ok(Job job, RequestScope requestScope, Optional<ChangeSpec> optional) {
        log.debug("team manage job {}", job.getId());
        List<Team> teamList = job.getOrganization().getTeam();
        boolean isServiceAccount = authenticatedUser.isServiceAccount(requestScope.getUser());
        boolean isWorkspaceLock = job.getWorkspace().isLocked();
        if(isWorkspaceLock){
            log.warn("Workspace {} {} is locked, job creation is denied", job.getWorkspace().getId(), job.getWorkspace().getName());
        }
        for (Team team : teamList) {
            if (isServiceAccount){
                if (groupService.isServiceMember(requestScope.getUser(), team.getName()) && team.isManageWorkspace() && !isWorkspaceLock ){
                    return true;
                }
            } else {
                if (groupService.isMember(requestScope.getUser(), team.getName()) && team.isManageWorkspace() && !isWorkspaceLock)
                    return true;
            }
        }
        return false;
    }
}
