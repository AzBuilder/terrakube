package io.terrakube.api.rs.checks.user;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.User;
import com.yahoo.elide.core.security.checks.UserCheck;
import io.terrakube.api.plugin.security.groups.GroupService;
import io.terrakube.api.plugin.security.user.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@SecurityCheck(IsSuperUser.RULE)
public class IsSuperUser extends UserCheck {

    public static final String RULE = "user is a superuser";

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Autowired
    GroupService groupService;

    @Value("${io.terrakube.owner}")
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

