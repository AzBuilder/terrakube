package org.terrakube.api.rs.checks.user;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.User;
import com.yahoo.elide.core.security.checks.UserCheck;
import lombok.extern.slf4j.Slf4j;
import org.terrakube.api.plugin.security.groups.GroupService;
import org.terrakube.api.plugin.security.user.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@SecurityCheck(IsSuperUser.RULE)
public class IsSuperUser extends UserCheck {

    public static final String RULE = "user is a superuser";

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Autowired
    GroupService groupService;

    @Value("${org.terrakube.owner}")
    private String instanceOwner;

    @Override
    public boolean ok(User user) {
        if (authenticatedUser.isServiceAccount(user)){
            return groupService.isServiceMember(user, instanceOwner);
        }else{
            return groupService.isMember(user, instanceOwner);
        }
    }
}

