package org.terrakube.api.plugin.security.groups;

import com.yahoo.elide.core.security.User;

public interface GroupService {

    boolean isMember(User user, String group);

    boolean isServiceMember(User user, String group);
}
