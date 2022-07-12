package org.terrakube.api.plugin.security.audit.dex;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Slf4j
public class DexAuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            log.info("getCurrentAuditor: {}", authentication);
            if (authentication instanceof AnonymousAuthenticationToken)
                return Optional.of("AnonymousUser");
            else
                return Optional.of(isServiceAccount(authentication) ? "serviceAccount" : getEmail(authentication));
        } else {
            return Optional.of("Internal");
        }
    }

    public String getEmail(Authentication authentication) {
        org.springframework.security.oauth2.jwt.Jwt principal = (org.springframework.security.oauth2.jwt.Jwt) authentication.getPrincipal();
        return (String) principal.getClaims().get("email");
    }

    public boolean isServiceAccount(Authentication authentication) {
        return false;
    }
}
