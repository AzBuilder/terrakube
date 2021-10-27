package org.azbuilder.registry.configuration.security.authentication.azure;

import com.azure.spring.aad.webapi.AADResourceServerWebSecurityConfigurerAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ConditionalOnProperty(prefix = "org.azbuilder.api.authentication", name = "type", havingValue = "AZURE")
public class AADOAuth2ResourceServerSecurityConfig extends AADResourceServerWebSecurityConfigurerAdapter {
    /**
     * Add configuration logic as needed.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        //http.authorizeRequests(requests -> requests.anyRequest().authenticated());
        http.authorizeRequests()
                .antMatchers("/.well-known/**").permitAll()
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated();
    }
}
