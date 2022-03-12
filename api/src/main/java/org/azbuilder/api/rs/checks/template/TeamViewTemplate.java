package org.azbuilder.api.rs.checks.template;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.security.groups.GroupService;
import org.azbuilder.api.plugin.security.user.AuthenticatedUser;
import org.azbuilder.api.rs.team.Team;
import org.azbuilder.api.rs.template.Template;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Slf4j
@SecurityCheck(TeamViewTemplate.RULE)
public class TeamViewTemplate extends OperationCheck<Template> {
    public static final String RULE = "team view template";

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Autowired
    GroupService groupService;

    @Override
    public boolean ok(Template template, RequestScope requestScope, Optional<ChangeSpec> optional) {
        log.info("team view template {}", template.getId());
        boolean isServiceAccount = authenticatedUser.isServiceAccount(requestScope.getUser());
        List<Team> teamList = template.getOrganization().getTeam();
        if (authenticatedUser.isSuperUser(requestScope.getUser())) {
            return true;
        } else {
            for (Team team : teamList) {
                if (isServiceAccount){
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
