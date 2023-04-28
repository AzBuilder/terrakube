package org.terrakube.api.rs.checks.provider;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;
import lombok.extern.slf4j.Slf4j;
import org.terrakube.api.plugin.security.groups.GroupService;
import org.terrakube.api.plugin.security.user.AuthenticatedUser;
import org.terrakube.api.rs.provider.Provider;
import org.terrakube.api.rs.team.Team;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Slf4j
@SecurityCheck(TeamManageProvider.RULE)
public class TeamManageProvider extends OperationCheck<Provider> {

    public static final String RULE = "team manage provider";

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Autowired
    GroupService groupService;

    @Override
    public boolean ok(Provider provider, RequestScope requestScope, Optional<ChangeSpec> optional) {
        log.debug("team manage provider {}", provider.getId());
        boolean isServiceAccount = authenticatedUser.isServiceAccount(requestScope.getUser());
        List<Team> teamList = provider.getOrganization().getTeam();
        for (Team team : teamList) {
            if (isServiceAccount){
                if (groupService.isServiceMember(requestScope.getUser(), team.getName()) && team.isManageProvider() ){
                    return true;
                }
            } else {
                if (groupService.isMember(requestScope.getUser(), team.getName()) && team.isManageProvider())
                    return true;
            }
        }
        return false;
    }
}