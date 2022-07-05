package org.terrakube.api.plugin.security.authentication.dex;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class ApiKeyAuthManager implements AuthenticationManager {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("{}",authentication.getPrincipal().getClass().getName());
        String principal = (String) authentication.getPrincipal();
        log.info("{}", principal);
        if(authentication.getPrincipal().getClass().getName().equals("java.lang.String")) {
            log.info("authenticated");
            authentication.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return authentication;
        }else{
            return null;
        }
    }
}
