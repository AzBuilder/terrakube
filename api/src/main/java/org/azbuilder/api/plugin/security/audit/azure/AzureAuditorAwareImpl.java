package org.azbuilder.api.plugin.security.audit.azure;

import com.azure.spring.aad.AADOAuth2AuthenticatedPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Slf4j
public class AzureAuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        log.info("getCurrentAuditor");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        return Optional.of(isServiceAccount(authentication) ? "serviceAccount" : getEmail(authentication));
    }

    private AADOAuth2AuthenticatedPrincipal getAzureAdPrincipal(Authentication authentication) {
        org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication bearerTokenAuthenticationToken = (org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication) authentication;
        return (AADOAuth2AuthenticatedPrincipal) bearerTokenAuthenticationToken.getPrincipal();
    }

    public String getEmail(Authentication authentication) {
        return (String) getAzureAdPrincipal(authentication).getAttributes().get("unique_name");
    }

    // Azure AD Token Documentation https://docs.microsoft.com/en-us/azure/active-directory/develop/access-tokens
    public boolean isServiceAccount(Authentication authentication) {
        AADOAuth2AuthenticatedPrincipal aadoAuth2AuthenticatedPrincipal = getAzureAdPrincipal(authentication);
        return ((String) aadoAuth2AuthenticatedPrincipal.getAttributes().get("appidacr")).equals("1");
    }
}
