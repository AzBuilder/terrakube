package org.terrakube.api.plugin.security.authentication.dex;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class DexWebSecurityAdapter {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, @Value("${org.terrakube.token.issuer-uri}") String issuerUri, @Value("${org.terrakube.token.pat}") String patJwtSecret, @Value("${org.terrakube.token.internal}") String internalJwtSecret) throws Exception {
        http.cors().and().authorizeRequests(authz -> authz
                        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .antMatchers("/actuator/**").permitAll()
                        .antMatchers("/callback/v1/**").permitAll()
                        .antMatchers("/.well-known/terraform.json").permitAll()
                        .antMatchers("/remote/tfe/v2/ping").permitAll()
                        .antMatchers(HttpMethod.PUT, "/remote/tfe/v2/configuration-versions/*").permitAll()
                        .antMatchers("/remote/tfe/v2/plans/*/logs").permitAll()
                        .antMatchers("/remote/tfe/v2/applies/*/logs").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> {
                    AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver = DexAuthenticationManagerResolver
                            .builder()
                            .dexIssuerUri(issuerUri)
                            .patJwtSecret(patJwtSecret)
                            .internalJwtSecret(internalJwtSecret)
                            .build();
                    oauth2.authenticationManagerResolver(authenticationManagerResolver);
                });

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${org.terrakube.ui.url:http://localhost:3000}") String uiURL) {
        log.info("Loading CORS {}", uiURL);
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(uiURL.split(",")));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Access-Control-Allow-Headers", "Access-Control-Allow-Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers", "Origin", "Cache-Control", "Content-Type", "Authorization"));
        configuration.setAllowedMethods(Arrays.asList("DELETE", "GET", "POST", "PATCH", "PUT", "OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
