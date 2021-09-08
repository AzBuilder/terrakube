package org.azbuilder.api.rs.checks.workspace;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.security.groups.GroupService;
import org.azbuilder.api.plugin.security.user.AuthenticatedUser;
import org.azbuilder.api.rs.team.Team;
import org.azbuilder.api.rs.workspace.Workspace;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Slf4j
@SecurityCheck(TeamManageWorkspace.RULE)
public class TeamManageWorkspace extends OperationCheck<Workspace> {

    public static final String RULE = "team manage workspace";

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Autowired
    GroupService groupService;

    @Override
    public boolean ok(Workspace workspace, RequestScope requestScope, Optional<ChangeSpec> optional) {
        log.info("team manage workspace {}", workspace.getId());
        List<Team> teamList = workspace.getOrganization().getTeam();
        for (Team team : teamList) {
             if(groupService.isMember(authenticatedUser.getEmail(requestScope.getUser()), team.getName()) && team.isManageWorkspace())
                return true;
        }
        return false;
    }
}
