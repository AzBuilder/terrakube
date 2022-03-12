package org.azbuilder.api.plugin.security.user;

import com.yahoo.elide.core.security.User;

public interface AuthenticatedUser {

    String getEmail(User user);

    String getApplication(User user);

    public boolean isServiceAccount(User user);

    public boolean isSuperUser(User user);
}
