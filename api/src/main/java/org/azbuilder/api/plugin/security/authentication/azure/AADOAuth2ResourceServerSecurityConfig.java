package org.azbuilder.api.plugin.security.authentication.azure;

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

    @Value( "${org.terrakube.ui.url:http://localhost:3000}" )
    private String uiURL;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        //http.authorizeRequests(requests -> requests.anyRequest().authenticated());
        http.cors().and().authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                .antMatchers("/callback/v1/**").permitAll()
                .antMatchers("/doc").permitAll()
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated();
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
