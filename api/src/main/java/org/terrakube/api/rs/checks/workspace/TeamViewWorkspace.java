package org.terrakube.api.rs.checks.workspace;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;
import lombok.extern.slf4j.Slf4j;
import org.terrakube.api.plugin.security.user.AuthenticatedUser;
import org.terrakube.api.rs.checks.membership.MembershipService;
import org.terrakube.api.rs.team.Team;
import org.terrakube.api.rs.workspace.Workspace;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Slf4j
@SecurityCheck(TeamViewWorkspace.RULE)
public class TeamViewWorkspace extends OperationCheck<Workspace> {

    public static final String RULE = "team view workspace";

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Autowired
    MembershipService membershipService;

    @Override
    public boolean ok(Workspace workspace, RequestScope requestScope, Optional<ChangeSpec> optional) {
        log.info("team view workspace {}", workspace.getId());
        List<Team> teamList = workspace.getOrganization().getTeam();
        return authenticatedUser.isSuperUser(requestScope.getUser()) ? true : membershipService.checkMembership(requestScope.getUser(), teamList);
    }

}
