package io.terrakube.api.rs.checks.collection;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import io.terrakube.api.plugin.security.user.AuthenticatedUser;
import io.terrakube.api.rs.checks.membership.MembershipService;
import io.terrakube.api.rs.collection.Collection;
import io.terrakube.api.rs.team.Team;

import java.util.List;
import java.util.Optional;

@Slf4j
@SecurityCheck(TeamViewCollection.RULE)
public class TeamViewCollection extends OperationCheck<Collection> {
    public static final String RULE = "team view collection";

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Autowired
    MembershipService membershipService;

    @Override
    public boolean ok(Collection collection, RequestScope requestScope, Optional<ChangeSpec> optional) {
        log.debug("team view template {}", collection.getId());
        List<Team> teamList = collection.getOrganization().getTeam();
        return authenticatedUser.isSuperUser(requestScope.getUser()) ? true : membershipService.checkMembership(requestScope.getUser(), teamList);
    }
}
