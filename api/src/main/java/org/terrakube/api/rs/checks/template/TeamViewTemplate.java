package org.terrakube.api.rs.checks.template;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;
import lombok.extern.slf4j.Slf4j;
import org.terrakube.api.plugin.security.user.AuthenticatedUser;
import org.terrakube.api.rs.checks.membership.MembershipService;
import org.terrakube.api.rs.team.Team;
import org.terrakube.api.rs.template.Template;
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
    MembershipService membershipService;

    @Override
    public boolean ok(Template template, RequestScope requestScope, Optional<ChangeSpec> optional) {
        log.debug("team view template {}", template.getId());
        List<Team> teamList = template.getOrganization().getTeam();
        return authenticatedUser.isSuperUser(requestScope.getUser()) ? true : membershipService.checkMembership(requestScope.getUser(), teamList);
    }
}
