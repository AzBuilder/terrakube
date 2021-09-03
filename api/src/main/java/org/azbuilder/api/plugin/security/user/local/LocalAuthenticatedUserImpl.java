package org.azbuilder.api.plugin.security.user.local;

import com.yahoo.elide.core.security.User;
import org.azbuilder.api.plugin.security.user.AuthenticatedUser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "org.azbuilder.api.users", name = "type", havingValue = "Local")
public class LocalAuthenticatedUserImpl implements AuthenticatedUser {

    private static final String LOCAL_USER="local@user.com";

    @Override
    public String getEmail(User user) {
        return LOCAL_USER;
    }

    @Override
    public boolean isServiceAccount(User user) {
        return true;
    }
}
