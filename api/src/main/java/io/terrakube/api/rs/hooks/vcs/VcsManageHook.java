package io.terrakube.api.rs.hooks.vcs;

import com.yahoo.elide.annotation.LifeCycleHookBinding;
import com.yahoo.elide.core.lifecycle.LifeCycleHook;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import io.terrakube.api.plugin.vcs.TokenService;
import io.terrakube.api.rs.vcs.Vcs;
import io.terrakube.api.rs.vcs.VcsType;

import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class VcsManageHook implements LifeCycleHook<Vcs> {

    @Autowired
    TokenService tokenService;

    @Override
    public void execute(LifeCycleHookBinding.Operation operation, LifeCycleHookBinding.TransactionPhase phase, Vcs vcs, RequestScope requestScope, Optional<ChangeSpec> changes) {
        log.info("VcsManageHook {}", vcs.getId());
        switch (operation) {
            case CREATE:
                if (vcs.getVcsType().equals(VcsType.AZURE_SP_MI)) {
                    log.info("Create vcs schedule for AZURE_SP_DYNAMIC");
                    tokenService.generateAccessToken(vcs.getId().toString(), "n/a");
                }
                break;
            default:
                log.info("Not supported");
                break;
        }
    }
}
