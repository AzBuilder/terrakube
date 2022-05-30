package org.azbuilder.api.rs.checks.globalvar;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.security.user.AuthenticatedUser;
import org.azbuilder.api.rs.globalvar.Globalvar;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Slf4j
@SecurityCheck(AdminReadSecret.RULE)
public class AdminReadSecret extends OperationCheck<Globalvar> {

    public static final String RULE = "admin read secret";

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Override
    public boolean ok(Globalvar globalvar, RequestScope requestScope, Optional<ChangeSpec> optional) {
        if(globalvar.isSensitive())
            return false;
        else
            return true;
    }
}
