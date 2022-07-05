package org.terrakube.api.plugin.security.authentication.dex;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class ApiKeyAuthFilter extends AbstractPreAuthenticatedProcessingFilter {

    private static final String API_KEY_HEADER = "API_KEY";

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        log.info("ApiKeyAuthFilter");
        return request.getHeader(API_KEY_HEADER);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return null;
    }
}
