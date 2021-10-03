package org.azbuilder.api.rs.hooks.vcs;

import com.yahoo.elide.annotation.LifeCycleHookBinding;
import com.yahoo.elide.core.lifecycle.LifeCycleHook;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.security.user.AuthenticatedUser;
import org.azbuilder.api.plugin.vcs.TokenService;
import org.azbuilder.api.rs.vcs.Vcs;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class VcsReadTokenHook implements LifeCycleHook<Vcs> {
    @Autowired
    TokenService tokenService;

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Override
    public void execute(LifeCycleHookBinding.Operation operation, LifeCycleHookBinding.TransactionPhase transactionPhase, Vcs vcs, RequestScope requestScope, Optional<ChangeSpec> optional) {
        if (authenticatedUser.isServiceAccount(requestScope.getUser())) {
            log.info("Checking if accessToken is valid for Service Account");
            Map<String, Object> newTokenInformation = tokenService.refreshAccessToken(vcs.getId().toString(), vcs.getVcsType(), vcs.getTokenExpiration(), vcs.getClientId(), vcs.getClientSecret(), vcs.getRefreshToken(), vcs.getAccessToken());
            if (!newTokenInformation.isEmpty()) {
                vcs.setAccessToken((String) newTokenInformation.get("accessToken"));
                vcs.setRefreshToken((String) newTokenInformation.get("refreshToken"));
                vcs.setTokenExpiration((Date) newTokenInformation.get("tokenExpiration"));
            }
        }
    }
}
