package org.azbuilder.api.plugin.security.user.azure;

import com.azure.spring.aad.webapi.AADOAuth2AuthenticatedPrincipal;
import com.yahoo.elide.core.security.User;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.security.user.AuthenticatedUser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "org.azbuilder.api.users", name = "type", havingValue = "AZURE")
public class AzureAuthenticatedUserImpl implements AuthenticatedUser {

    private AADOAuth2AuthenticatedPrincipal getAzureAdPrincipal(User user){
        org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication bearerTokenAuthenticationToken = (org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication) user.getPrincipal();
        return (AADOAuth2AuthenticatedPrincipal) bearerTokenAuthenticationToken.getPrincipal();
    }

    @Override
    public String getEmail(User user) {
        return (String) getAzureAdPrincipal(user).getAttributes().get("upn");
    }

    // Azure AD Token Documentation https://docs.microsoft.com/en-us/azure/active-directory/develop/access-tokens
    @Override
    public boolean isServiceAccount(User user){
        AADOAuth2AuthenticatedPrincipal aadoAuth2AuthenticatedPrincipal = getAzureAdPrincipal(user);
        return ( (String) aadoAuth2AuthenticatedPrincipal.getAttributes().get("appidacr")).equals("1")? true : false;
    }
}
