package org.terrakube.api.rs.checks.globalvar;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;
import org.terrakube.api.plugin.security.user.AuthenticatedUser;
import org.terrakube.api.rs.globalvar.Globalvar;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

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
