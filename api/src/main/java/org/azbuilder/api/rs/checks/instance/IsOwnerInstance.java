package org.azbuilder.api.rs.checks.instance;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;
import org.azbuilder.api.plugin.security.groups.GroupService;
import org.azbuilder.api.plugin.security.user.AuthenticatedUser;
import org.azbuilder.api.rs.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

@SecurityCheck(IsOwnerInstance.RULE)
public class IsOwnerInstance extends OperationCheck<Organization> {

    public static final String RULE = "owner instance";

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Autowired
    GroupService groupService;

    @Value( "${org.azbuilder.owner}" )
    private String instanceOwner;

    @Override
    public boolean ok(Organization organization, RequestScope requestScope, Optional<ChangeSpec> optional) {
        return groupService.isMember(authenticatedUser.getEmail(requestScope.getUser()), instanceOwner);
    }
}

