package org.terrakube.api.rs.checks.vcs;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;
import lombok.extern.slf4j.Slf4j;
import org.terrakube.api.plugin.security.user.AuthenticatedUser;
import org.terrakube.api.rs.vcs.Vcs;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Slf4j
@SecurityCheck(ReadVcsSecret.RULE)
public class ReadVcsSecret extends OperationCheck<Vcs> {
    public static final String RULE = "read vcs secret";

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Override
    public boolean ok(Vcs vcs, RequestScope requestScope, Optional<ChangeSpec> optional) {
        log.debug("user view vcs {}", vcs.getId());
        return false;
    }
}
