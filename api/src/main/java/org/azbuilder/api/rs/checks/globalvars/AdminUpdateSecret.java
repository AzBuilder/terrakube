package org.azbuilder.api.rs.checks.globalvars;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.rs.globalvars.Vars;

import java.util.Optional;

@Slf4j
@SecurityCheck(AdminUpdateSecret.RULE)
public class AdminUpdateSecret extends OperationCheck<Vars> {

    public static final String RULE = "admin update secret";

    @Override
    public boolean ok(Vars vars, RequestScope requestScope, Optional<ChangeSpec> optional) {
        return false;
    }
}
