package org.terrakube.api.rs.checks.user;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.User;
import com.yahoo.elide.core.security.checks.UserCheck;
import org.terrakube.api.plugin.security.user.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;


@Deprecated
@SecurityCheck(IsServiceUser.RULE)
public class IsServiceUser extends UserCheck {

    public static final String RULE = "user is a service";

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Override
    public boolean ok(User user) {
        return authenticatedUser.isServiceAccount(user);
    }
}