package org.azbuilder.api.plugin.security.user.local;

import com.yahoo.elide.core.security.User;
import org.azbuilder.api.plugin.security.user.AuthenticatedUser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "org.azbuilder.api.users", name = "type", havingValue = "LOCAL")
public class LocalAuthenticatedUserImpl implements AuthenticatedUser {

    private static final String LOCAL_USER="local@user.com";
    private static final String LOCAL_APPLICATION="0";

    @Override
    public String getEmail(User user) {
        return LOCAL_USER;
    }

    @Override
    public String getApplication(User user) { return LOCAL_APPLICATION; }

    @Override
    public boolean isServiceAccount(User user) {
        return true;
    }

    @Override
    public boolean isSuperUser(User user) {
        return true;
    }
}
