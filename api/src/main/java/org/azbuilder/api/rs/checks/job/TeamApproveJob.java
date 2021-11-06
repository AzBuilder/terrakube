package org.azbuilder.api.rs.checks.job;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.security.groups.GroupService;
import org.azbuilder.api.plugin.security.user.AuthenticatedUser;
import org.azbuilder.api.rs.job.Job;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Slf4j
@SecurityCheck(TeamApproveJob.RULE)
public class TeamApproveJob extends OperationCheck<Job> {
    public static final String RULE = "team approve job";

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Autowired
    GroupService groupService;

    @Override
    public boolean ok(Job job, RequestScope requestScope, Optional<ChangeSpec> optional) {
        if (job.getApprovalTeam() == null)
            return true;
        else {
            return groupService.isMember(authenticatedUser.getEmail(requestScope.getUser()), job.getApprovalTeam())
        }
    }
}
