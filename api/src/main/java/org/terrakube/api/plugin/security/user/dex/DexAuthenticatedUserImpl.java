package org.terrakube.api.plugin.security.user.dex;

import com.yahoo.elide.core.security.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.terrakube.api.plugin.security.groups.GroupService;
import org.terrakube.api.plugin.security.user.AuthenticatedUser;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "org.terrakube.api.users", name = "type", havingValue = "DEX")
public class DexAuthenticatedUserImpl implements AuthenticatedUser {

    @Value("${org.terrakube.owner}")
    private String instanceOwner;

    @Autowired
    private GroupService groupService;

    private JwtAuthenticationToken getSecurityPrincipal(User user) {
        JwtAuthenticationToken principal = ((JwtAuthenticationToken) user.getPrincipal());
        return principal;
    }

    @Override
    public String getEmail(User user) {
        return (String) getSecurityPrincipal(user).getTokenAttributes().get("email");
    }

    @Override
    public String getApplication(User user) {
        return "dex";
    }

    @Override
    public boolean isServiceAccount(User user) {
        log.info("isServiceAccount {}", user.getPrincipal().getClass().getName());
        return false;
    }

    @Override
    public boolean isSuperUser(User user) {
        return groupService.isMember(user,instanceOwner);
    }

}
