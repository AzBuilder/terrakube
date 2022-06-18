package org.terrakube.api.rs.checks.provider;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;
import lombok.extern.slf4j.Slf4j;
import org.terrakube.api.plugin.security.user.AuthenticatedUser;
import org.terrakube.api.rs.checks.membership.MembershipService;
import org.terrakube.api.rs.provider.Provider;
import org.terrakube.api.rs.team.Team;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Slf4j
@SecurityCheck(TeamViewProvider.RULE)
public class TeamViewProvider extends OperationCheck<Provider> {

    public static final String RULE = "team view provider";

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Autowired
    MembershipService membershipService;

    @Override
    public boolean ok(Provider provider, RequestScope requestScope, Optional<ChangeSpec> optional) {
        log.info("team view provider {}", provider.getId());
        List<Team> teamList = provider.getOrganization().getTeam();
        return authenticatedUser.isSuperUser(requestScope.getUser()) ? true : membershipService.checkMembership(requestScope.getUser(), teamList);
    }
}