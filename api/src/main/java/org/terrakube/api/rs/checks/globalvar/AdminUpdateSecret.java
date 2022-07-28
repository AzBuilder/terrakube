package org.terrakube.api.rs.checks.globalvar;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;
import org.terrakube.api.rs.globalvar.Globalvar;

import java.util.Optional;

@SecurityCheck(AdminUpdateSecret.RULE)
public class AdminUpdateSecret extends OperationCheck<Globalvar> {

    public static final String RULE = "admin update secret";

    @Override
    public boolean ok(Globalvar globalvar, RequestScope requestScope, Optional<ChangeSpec> optional) {
        return false;
    }
}
