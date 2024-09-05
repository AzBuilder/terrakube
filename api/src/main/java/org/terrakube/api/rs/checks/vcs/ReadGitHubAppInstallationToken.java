package org.terrakube.api.rs.checks.vcs;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.terrakube.api.plugin.security.user.AuthenticatedUser;
import org.terrakube.api.rs.vcs.GitHubAppToken;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;

@SecurityCheck(ReadGitHubAppInstallationToken.RULE)
public class ReadGitHubAppInstallationToken extends OperationCheck<GitHubAppToken> {
    public static final String RULE = "read github app installation token";

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Override
    public boolean ok(GitHubAppToken object, RequestScope requestScope, Optional<ChangeSpec> changeSpec) {
        if(authenticatedUser.isServiceAccountInternal(requestScope.getUser()) && authenticatedUser.isSuperUser(requestScope.getUser())){
            return true;
        }
        return false;
    }
}
