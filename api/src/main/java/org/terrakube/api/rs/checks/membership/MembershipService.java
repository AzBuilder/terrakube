package org.terrakube.api.rs.checks.membership;

import com.yahoo.elide.core.security.User;
import lombok.extern.slf4j.Slf4j;
import org.terrakube.api.plugin.security.groups.GroupService;
import org.terrakube.api.plugin.security.user.AuthenticatedUser;
import org.terrakube.api.rs.team.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MembershipService {

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Autowired
    GroupService groupService;

    public boolean checkMembership(User user, List<Team> teamList) {
        for (Team team : teamList) {
            if (authenticatedUser.isServiceAccount(user)) {
                String applicationName = authenticatedUser.getApplication(user);
                if (groupService.isServiceMember(applicationName, team.getName())) {
                    log.info("application {} is member of {}", applicationName, team.getName());
                    return true;
                }
            } else {
                String userName = authenticatedUser.getEmail(user);
                if (groupService.isMember(user, team.getName()))
                    log.info("user {} is member of {}", userName, team.getName());
                return true;
            }
        }
        return false;
    }

}
