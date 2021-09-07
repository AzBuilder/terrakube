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
        List<Team> teamList = module.getOrganization().getTeam();
        for (Team team : teamList) {
            if (groupService.isMember(authenticatedUser.getEmail(requestScope.getUser()), team.getName()) && team.isManageWorkspace())
                return true;
        }
        return false;
    }
}
