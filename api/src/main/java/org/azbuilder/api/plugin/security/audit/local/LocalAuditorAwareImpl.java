package org.azbuilder.api.plugin.security.audit.local;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class LocalAuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("local@demo.com");
    }
}
