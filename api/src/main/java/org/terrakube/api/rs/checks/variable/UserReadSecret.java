package org.terrakube.api.rs.checks.variable;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;
import lombok.extern.slf4j.Slf4j;
import org.terrakube.api.plugin.security.user.AuthenticatedUser;
import org.terrakube.api.rs.workspace.parameters.Variable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Slf4j
@SecurityCheck(UserReadSecret.RULE)
public class UserReadSecret extends OperationCheck<Variable> {

    public static final String RULE = "user read secret";

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Override
    public boolean ok(Variable variable, RequestScope requestScope, Optional<ChangeSpec> optional) {
        log.info("user view variable {}", variable.getId());
        if(variable.isSensitive()) {
            return false;
        }
        else
            return true;
    }


}
