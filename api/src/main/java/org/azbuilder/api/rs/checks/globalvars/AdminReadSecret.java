package org.azbuilder.api.rs.checks.globalvars;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.security.user.AuthenticatedUser;
import org.azbuilder.api.rs.globalvars.Vars;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Slf4j
@SecurityCheck(AdminReadSecret.RULE)
public class AdminReadSecret extends OperationCheck<Vars> {

    public static final String RULE = "admin read secret";

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Override
    public boolean ok(Vars vars, RequestScope requestScope, Optional<ChangeSpec> optional) {
        if(vars.isSensitive())
            return false;
        else
            return true;
    }
}
