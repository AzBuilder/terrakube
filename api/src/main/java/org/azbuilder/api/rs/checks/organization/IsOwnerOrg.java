package org.azbuilder.api.rs.checks.organization;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;
import org.azbuilder.api.plugin.security.groups.GroupService;
import org.azbuilder.api.plugin.security.user.AuthenticatedPrincipal;
import org.azbuilder.api.rs.Organization;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@SecurityCheck(IsOwnerOrg.RULE)
public class IsOwnerOrg extends OperationCheck<Organization> {

    public static final String RULE = "owner organization";

    @Autowired
    AuthenticatedPrincipal authenticatedPrincipal;

    @Autowired
    GroupService groupService;

    @Override
    public boolean ok(Organization organization, RequestScope requestScope, Optional<ChangeSpec> optional) {
        return groupService.isMember(authenticatedPrincipal.getPrincipal(requestScope.getUser()), organization.getOwner());
    }
}

