package io.terrakube.api.rs.checks.user;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.User;
import com.yahoo.elide.core.security.checks.UserCheck;
import lombok.extern.slf4j.Slf4j;
import io.terrakube.api.plugin.security.groups.GroupService;
import io.terrakube.api.plugin.security.user.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@SecurityCheck(isSuperService.RULE)
public class isSuperService extends UserCheck {

    public static final String RULE = "user is a super service";

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Autowired
    GroupService groupService;

    @Value("${io.terrakube.owner}")
    private String instanceOwner;

    @Override
    public boolean ok(User user) {
        if (authenticatedUser.isServiceAccount(user)) {
            return groupService.isServiceMember(user, instanceOwner);
        } else {
            return false;
        }
    }
}
