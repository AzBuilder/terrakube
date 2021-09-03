package org.azbuilder.api.plugin.security.user;

import com.yahoo.elide.core.security.User;

public interface AuthenticatedUser {

    String getEmail(User user);

    public boolean isServiceAccount(User user);
}
