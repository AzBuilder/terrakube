package org.terrakube.api.plugin.security.groups;

import com.yahoo.elide.core.security.User;
import org.terrakube.api.rs.Organization;

public interface GroupService {

    boolean isMember(User user, String group);

    boolean isServiceMember(User user, String group);

    boolean isMemberWithLimitedAccess(User user, Object elideEntity);

}
