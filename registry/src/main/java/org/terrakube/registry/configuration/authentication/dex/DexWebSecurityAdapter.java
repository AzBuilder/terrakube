package org.terrakube.registry.configuration.authentication.dex;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@ConditionalOnProperty(prefix = "org.terrakube.api.authentication", name = "type", havingValue = "DEX")
public class DexWebSecurityAdapter extends WebSecurityConfigurerAdapter {

    @Value( "${org.terrakube.ui.fqdn:http://localhost:3000}" )
    private String uiDomain;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().authorizeRequests(authz -> authz
                        .antMatchers("/.well-known/**").permitAll()
                        .antMatchers("/actuator/**").permitAll()
                        .antMatchers("/terraform/modules/v1/download/**").permitAll()
                        .antMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt());
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        log.info("CORS for UI Domain {}", uiDomain);
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(uiDomain.split(",")));
        configuration.setAllowedHeaders(Arrays.asList("Access-Control-Allow-Headers", "Access-Control-Allow-Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers", "Origin", "Cache-Control", "Content-Type", "Authorization"));
        configuration.setAllowedMethods(Arrays.asList("DELETE", "GET", "POST", "PATCH", "PUT", "OPTIONS"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
