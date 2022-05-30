package org.azbuilder.registry.configuration.security.authentication.azure;

import com.azure.spring.aad.webapi.AADResourceServerWebSecurityConfigurerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ConditionalOnProperty(prefix = "org.azbuilder.api.authentication", name = "type", havingValue = "AZURE")
@Slf4j
public class AADOAuth2ResourceServerSecurityConfig extends AADResourceServerWebSecurityConfigurerAdapter {

    @Value( "${org.terrakube.ui.fqdn:http://localhost:3000}" )
    private String uiDomain;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.cors().and().authorizeRequests()
                .antMatchers("/.well-known/**").permitAll()
                .antMatchers("/terraform/modules/v1/download/**").permitAll()
                .antMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated();
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
