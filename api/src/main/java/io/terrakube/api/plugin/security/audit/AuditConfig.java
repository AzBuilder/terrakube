package io.terrakube.api.plugin.security.audit;

import io.terrakube.api.plugin.security.audit.dex.DexAuditorAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
class AuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new DexAuditorAwareImpl();
    }
}
