package org.azbuilder.api.rs.checks;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;
import org.azbuilder.api.plugin.security.AzureAuthenticatedPrincipal;
import org.azbuilder.api.rs.workspace.parameters.Variable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@SecurityCheck(IsSvcReadingSecret.RULE)
public class IsSvcReadingSecret extends OperationCheck<Variable> {

    public static final String RULE = "svc is reading secrets";

    @Autowired
    AzureAuthenticatedPrincipal azureAuthenticatedPrincipal;

    @Override
    public boolean ok(Variable variable, RequestScope requestScope, Optional<ChangeSpec> optional) {
        if(variable.isSensitive()) {
            return (azureAuthenticatedPrincipal.isServiceAccount(requestScope.getUser())) ? true: false;
        }
        else
            return true;
    }


}
