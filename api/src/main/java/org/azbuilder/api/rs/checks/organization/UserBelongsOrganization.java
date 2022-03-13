package org.azbuilder.api.rs.checks.organization;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.User;
import com.yahoo.elide.core.security.checks.OperationCheck;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.security.groups.GroupService;
import org.azbuilder.api.plugin.security.user.AuthenticatedUser;
import org.azbuilder.api.rs.Organization;
import org.azbuilder.api.rs.team.Team;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Slf4j
@SecurityCheck(UserBelongsOrganization.RULE)
public class UserBelongsOrganization extends OperationCheck<Organization> {

    public static final String RULE = "user belongs organization";

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Autowired
    GroupService groupService;

    @Override
    public boolean ok(Organization organization, RequestScope requestScope, Optional<ChangeSpec> optional) {
        if(authenticatedUser.isSuperUser(requestScope.getUser())){
            return true;
        }else{
            return isMemberOrganization(requestScope.getUser(), organization);
        }
    }

    /**
     * Review is the authenticated user belongs to the organization by searching the membership in each organization team
     * @param user
     * @param organization
     * @return
     */
    private boolean isMemberOrganization(User user, Organization organization){
        boolean isServiceAccount=authenticatedUser.isServiceAccount(user);
        String applicationName="";
        String userName="";
        if (isServiceAccount){
            applicationName = authenticatedUser.getApplication(user);
        }else{
            userName = authenticatedUser.getEmail(user);
        }

        List<Team> teamList = organization.getTeam();
        for (Team team : teamList) {
            if (isServiceAccount) {
                log.info("isServiceMember {} {}", team.getName(), groupService.isServiceMember(applicationName, team.getName()));
                if (groupService.isServiceMember(applicationName, team.getName())) {
                    return true;
                }
            } else {
                log.info("isMember {} {}", team.getName(), groupService.isMember(userName, team.getName()));
                if (groupService.isMember(userName, team.getName()))
                    return true;
            }
        }
        return false;
    }


}
