package org.azbuilder.api.rs.checks;

import com.azure.spring.aad.webapi.AADOAuth2AuthenticatedPrincipal;
import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;
import org.azbuilder.api.rs.workspace.parameters.Variable;

import java.util.Optional;

@SecurityCheck(IsSvcAccountReading.TRUE)
public class IsSvcAccountReading extends OperationCheck<Variable> {

    public static final String TRUE = "service account is reading";

    @Override
    public boolean ok(Variable variable, RequestScope requestScope, Optional<ChangeSpec> optional) {
        org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication bearerTokenAuthenticationToken = (org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication) requestScope.getUser().getPrincipal();
        AADOAuth2AuthenticatedPrincipal aadoAuth2AuthenticatedPrincipal = (AADOAuth2AuthenticatedPrincipal) bearerTokenAuthenticationToken.getPrincipal();
        System.out.println(aadoAuth2AuthenticatedPrincipal.getAttributes().get("appidacr"));
        String value= (String) aadoAuth2AuthenticatedPrincipal.getAttributes().get("appidacr");
        System.out.println(variable.getValue());
        variable.setValue("XXX");
        if(value.equals("0"))
            return true;
        else
            return false;
    }
}
