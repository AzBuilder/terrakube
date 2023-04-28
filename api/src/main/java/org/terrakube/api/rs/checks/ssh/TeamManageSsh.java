package org.terrakube.api.rs.checks.ssh;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.terrakube.api.plugin.security.groups.GroupService;
import org.terrakube.api.plugin.security.user.AuthenticatedUser;
import org.terrakube.api.rs.ssh.Ssh;
import org.terrakube.api.rs.team.Team;

import java.util.Optional;

@Slf4j
@SecurityCheck(TeamManageSsh.RULE)
public class TeamManageSsh extends OperationCheck<Ssh> {
    public static final String RULE = "team manage ssh";

    @Autowired
    GroupService groupService;
    @Autowired
    AuthenticatedUser authenticatedUser;

    @Override
    public boolean ok(Ssh ssh, RequestScope requestScope, Optional<ChangeSpec> optional) {
        log.debug("team manage ssh {}", ssh.getId());
        boolean isServiceAccount = authenticatedUser.isServiceAccount(requestScope.getUser());
        for (Team team : ssh.getOrganization().getTeam()) {
            if (isServiceAccount){
                if (groupService.isServiceMember(requestScope.getUser(), team.getName()) && team.isManageVcs() ){
                    return true;
                }
            } else {
                if (groupService.isMember(requestScope.getUser(), team.getName()) && team.isManageVcs())
                    return true;
            }
        }
        return false;
    }
}
