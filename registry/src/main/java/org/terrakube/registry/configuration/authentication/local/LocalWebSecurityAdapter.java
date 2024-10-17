package org.terrakube.registry.configuration.authentication.local;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(prefix = "org.terrakube.registry.authentication", name = "type", havingValue = "LOCAL")
public class LocalWebSecurityAdapter{

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/**").permitAll()
            );

        return http.build();
    }

}
