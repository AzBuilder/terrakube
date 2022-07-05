package org.terrakube.api.plugin.security.authentication.dex;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@ConditionalOnProperty(prefix = "org.terrakube.api.authentication", name = "type", havingValue = "DEX")
public class OAuth2LoginSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${org.terrakube.ui.url:http://localhost:3000}")
    private String uiURL;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        ApiKeyAuthFilter filter = new ApiKeyAuthFilter();
        filter.setAuthenticationManager(new ApiKeyAuthManager());


        http.cors().and().authorizeRequests(authz -> authz
                        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .antMatchers("/actuator/**").permitAll()
                        .antMatchers("/callback/v1/**").permitAll()
                        .antMatchers("/doc").permitAll()
                        .antMatchers("/keys/v1/generate").permitAll()
                )
                .oauth2ResourceServer(oauth2 -> {
                    log.info("{}", oauth2);
                    oauth2.authenticationManagerResolver(this.tokenAuthenticationManagerResolver());
                });

    }

    AuthenticationManagerResolver<HttpServletRequest> tokenAuthenticationManagerResolver() {
        return request -> {
            String token = request.getHeader("authorization");
            log.info("Token {}", token);
            int i = token.lastIndexOf('.');
            String withoutSignature = token.substring(0, i+1);
            Jwt<Header, Claims> untrusted = Jwts.parserBuilder().build().parseClaimsJwt(token.substring("Bearer ".length()));
            log.info("Issuer {}", untrusted.getBody().getIssuer());
            if (request.getHeader("API_KEY") != null) {
                log.info("Custom resolver");
                return new ApiKeyAuthManager();
            } else {
                log.info("JWT resolver");
                return new ProviderManager(new JwtAuthenticationProvider(JwtDecoders.fromIssuerLocation("https://dexidp.aks.vse.aespana.me")));
            }
        };
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
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
