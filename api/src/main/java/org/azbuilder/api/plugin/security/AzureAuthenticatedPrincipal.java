package org.azbuilder.api.plugin.security;

import com.azure.spring.aad.webapi.AADOAuth2AuthenticatedPrincipal;
import com.yahoo.elide.core.security.User;
import org.springframework.stereotype.Service;

@Service
public class AzureAuthenticatedPrincipal {

    public AADOAuth2AuthenticatedPrincipal get(User user){
        org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication bearerTokenAuthenticationToken = (org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication) user.getPrincipal();
        return (AADOAuth2AuthenticatedPrincipal) bearerTokenAuthenticationToken.getPrincipal();
    }

    // Azure AD Token Documentation https://docs.microsoft.com/en-us/azure/active-directory/develop/access-tokens
    public boolean isServiceAccount(User user){
        AADOAuth2AuthenticatedPrincipal aadoAuth2AuthenticatedPrincipal = get(user);
        return ( (String) aadoAuth2AuthenticatedPrincipal.getAttributes().get("appidacr")).equals("1")? true : false;
    }
}
