package org.azbuilder.registry.configuration.security.authentication.azure;

import com.azure.spring.aad.webapi.AADResourceServerWebSecurityConfigurerAdapter;
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

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ConditionalOnProperty(prefix = "org.azbuilder.api.authentication", name = "type", havingValue = "AZURE")
public class AADOAuth2ResourceServerSecurityConfig extends AADResourceServerWebSecurityConfigurerAdapter {

    @Value( "${org.terrakube.ui.fqdn:http://localhost:3000}" )
    private String uiURL;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        //http.authorizeRequests(requests -> requests.anyRequest().authenticated());
        http.cors().and().authorizeRequests()
                .antMatchers("/.well-known/**").permitAll()
                .antMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated();
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(uiURL));
        configuration.setAllowedMethods(Arrays.asList("PATCH","GET","HEAD","DELETE","POST","OPTIONS", "PUT"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
