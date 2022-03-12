package org.azbuilder.api.rs.checks.module;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.security.groups.GroupService;
import org.azbuilder.api.plugin.security.user.AuthenticatedUser;
import org.azbuilder.api.rs.module.Module;
import org.azbuilder.api.rs.team.Team;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Slf4j
@SecurityCheck(TeamViewModule.RULE)
public class TeamViewModule extends OperationCheck<Module> {

    public static final String RULE = "team view module";

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Autowired
    GroupService groupService;

    @Override
    public boolean ok(Module module, RequestScope requestScope, Optional<ChangeSpec> optional) {
        log.info("team view module {}", module.getId());
        boolean isServiceAccount = authenticatedUser.isServiceAccount(requestScope.getUser());
        List<Team> teamList = module.getOrganization().getTeam();
        if (authenticatedUser.isSuperUser(requestScope.getUser())) {
            return true;
        } else {
            for (Team team : teamList) {
                if (authenticatedUser.isServiceAccount(requestScope.getUser())){
                    if (groupService.isServiceMember(authenticatedUser.getApplication(requestScope.getUser()), team.getName()) ){
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
