package org.azbuilder.api.rs.hooks.vcs;

import com.yahoo.elide.annotation.LifeCycleHookBinding;
import com.yahoo.elide.core.lifecycle.LifeCycleHook;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.security.user.AuthenticatedUser;
import org.azbuilder.api.plugin.vcs.TokenService;
import org.azbuilder.api.repository.VcsRepository;
import org.azbuilder.api.rs.vcs.Vcs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Deprecated(since = "1.7.0", forRemoval = true)
@Slf4j
public class VcsReadTokenHook implements LifeCycleHook<Vcs> {
    @Autowired
    TokenService tokenService;

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Autowired
    VcsRepository vcsRepository;

    @Transactional
    @Override
    public void execute(LifeCycleHookBinding.Operation operation, LifeCycleHookBinding.TransactionPhase transactionPhase, Vcs vcs, RequestScope requestScope, Optional<ChangeSpec> optional) {
        if (authenticatedUser.isServiceAccount(requestScope.getUser())) {
            log.info("Checking if accessToken is valid for Service Account");
            Map<String, Object> newTokenInformation = tokenService.refreshAccessToken(vcs.getId().toString(), vcs.getVcsType(), vcs.getTokenExpiration(), vcs.getClientId(), vcs.getClientSecret(), vcs.getRefreshToken());
            if (!newTokenInformation.isEmpty()) {
                Vcs tempVcs = vcsRepository.getOne(vcs.getId());
                tempVcs.setAccessToken((String) newTokenInformation.get("accessToken"));
                tempVcs.setRefreshToken((String) newTokenInformation.get("refreshToken"));
                tempVcs.setTokenExpiration((Date) newTokenInformation.get("tokenExpiration"));
                vcsRepository.save(tempVcs);
            }
        }
    }
}
