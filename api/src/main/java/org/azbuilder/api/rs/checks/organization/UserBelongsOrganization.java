package org.azbuilder.api.rs.checks.organization;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.security.groups.GroupService;
import org.azbuilder.api.plugin.security.user.AuthenticatedUser;
import org.azbuilder.api.rs.Organization;
import org.azbuilder.api.rs.team.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Optional;

@Slf4j
@SecurityCheck(UserBelongsOrganization.RULE)
public class UserBelongsOrganization extends OperationCheck<Organization> {

    public static final String RULE = "user belongs organization";

    @Value("${org.azbuilder.owner}")
    private String instanceOwner;

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Autowired
    GroupService groupService;

    @Override
    public boolean ok(Organization organization, RequestScope requestScope, Optional<ChangeSpec> optional) {
        log.info("user view organization {}",authenticatedUser.getEmail(requestScope.getUser()));
        if (groupService.isMember(authenticatedUser.getEmail(requestScope.getUser()), instanceOwner)) {
            log.info("{} is super user", authenticatedUser.getEmail(requestScope.getUser()));
            return true;
        }
        else {
            log.info("{} is not super user", authenticatedUser.getEmail(requestScope.getUser()));
            List<Team> teamList = organization.getTeam();
            for (Team team : teamList) {
                log.info("isMember {} {}", team.getName(), groupService.isMember(authenticatedUser.getEmail(requestScope.getUser()), team.getName()));
                if(groupService.isMember(authenticatedUser.getEmail(requestScope.getUser()), team.getName()))
                    return true;
            }
        }
        return false;
    }
}
