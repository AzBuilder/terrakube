package org.azbuilder.api.rs.checks.workspace;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.User;
import com.yahoo.elide.core.security.checks.OperationCheck;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.security.groups.GroupService;
import org.azbuilder.api.plugin.security.user.AuthenticatedUser;
import org.azbuilder.api.rs.team.Team;
import org.azbuilder.api.rs.workspace.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Optional;

@Slf4j
@SecurityCheck(TeamViewWorkspace.RULE)
public class TeamViewWorkspace extends OperationCheck<Workspace> {

    public static final String RULE = "team view workspace";

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Autowired
    GroupService groupService;

    @Value("${org.azbuilder.owner}")
    private String instanceOwner;

    @Override
    public boolean ok(Workspace workspace, RequestScope requestScope, Optional<ChangeSpec> optional) {
        log.info("team view workspace {}", workspace.getId());
        if (authenticatedUser.isSuperUser(requestScope.getUser())) {
            return true;
        } else {
            List<Team> teamList = workspace.getOrganization().getTeam();
            for (Team team : teamList) {
                if (authenticatedUser.isServiceAccount(requestScope.getUser())) {
                    if (groupService.isServiceMember(authenticatedUser.getApplication(requestScope.getUser()), team.getName())) {
                        return true;
                    }
                } else {
                    if (groupService.isMember(authenticatedUser.getEmail(requestScope.getUser()), team.getName()))
                        return true;
                }
            }
            return false;
        }
    }

}
