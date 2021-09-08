package org.azbuilder.api.rs.checks.variable;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.security.user.AuthenticatedUser;
import org.azbuilder.api.rs.workspace.parameters.Variable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Slf4j
@SecurityCheck(ServiceReadSecret.RULE)
public class ServiceReadSecret extends OperationCheck<Variable> {

    public static final String RULE = "service read secret";

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Override
    public boolean ok(Variable variable, RequestScope requestScope, Optional<ChangeSpec> optional) {
        log.info("user view variable {}", variable.getId());
        if(variable.isSensitive()) {
            return authenticatedUser.isServiceAccount(requestScope.getUser());
        }
        else
            return true;
    }


}
