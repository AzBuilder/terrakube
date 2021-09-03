package org.azbuilder.api.plugin.security.groups;

import com.yahoo.elide.core.security.User;

public interface GroupService {

    boolean isMember(String user, String group);

}
