package org.azbuilder.api.rs.hooks.module;

import com.yahoo.elide.annotation.LifeCycleHookBinding;
import com.yahoo.elide.core.lifecycle.LifeCycleHook;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.vcs.TokenService;
import org.azbuilder.api.repository.VcsRepository;
import org.azbuilder.api.rs.module.Module;
import org.azbuilder.api.rs.vcs.Vcs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Deprecated(since = "1.7.0", forRemoval = true)
@Slf4j
public class ModuleReadTokenHook implements LifeCycleHook<Module> {

    @Autowired
    TokenService tokenService;

    @Autowired
    VcsRepository vcsRepository;

    @Transactional
    @Override
    public void execute(LifeCycleHookBinding.Operation operation, LifeCycleHookBinding.TransactionPhase transactionPhase, Module module, RequestScope requestScope, Optional<ChangeSpec> optional) {
        if(module.getVcs()!=null) {
            log.info("Checking if accessToken is valid");
            Map<String, Object> newTokenInformation = tokenService.refreshAccessToken(
                    module.getVcs().getId().toString(),
                    module.getVcs().getVcsType(),
                    module.getVcs().getTokenExpiration(),
                    module.getVcs().getClientId(),
                    module.getVcs().getClientSecret(),
                    module.getVcs().getRefreshToken()
            );
            if (!newTokenInformation.isEmpty()) {
                Vcs tempVcs = vcsRepository.getOne(module.getVcs().getId());
                tempVcs.setAccessToken((String) newTokenInformation.get("accessToken"));
                tempVcs.setRefreshToken((String) newTokenInformation.get("refreshToken"));
                tempVcs.setTokenExpiration((Date) newTokenInformation.get("tokenExpiration"));
                vcsRepository.save(tempVcs);
            }
        }
    }
}
