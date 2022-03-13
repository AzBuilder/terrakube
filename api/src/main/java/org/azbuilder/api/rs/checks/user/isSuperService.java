package org.azbuilder.api.rs.checks.user;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.User;
import com.yahoo.elide.core.security.checks.UserCheck;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.security.groups.GroupService;
import org.azbuilder.api.plugin.security.user.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@SecurityCheck(isSuperService.RULE)
public class isSuperService extends UserCheck {

    public static final String RULE = "user is a super service";

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Autowired
    GroupService groupService;

    @Value("${org.azbuilder.owner}")
    private String instanceOwner;

    @Override
    public boolean ok(User user) {
        if (authenticatedUser.isServiceAccount(user)) {
            return groupService.isServiceMember(authenticatedUser.getApplication(user), instanceOwner);
        } else {
            return false;
        }
    }
}
