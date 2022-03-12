package org.azbuilder.api.plugin.security.user.azure;

import com.azure.spring.aad.AADOAuth2AuthenticatedPrincipal;
import com.yahoo.elide.core.security.User;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.security.groups.GroupService;
import org.azbuilder.api.plugin.security.user.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "org.azbuilder.api.users", name = "type", havingValue = "AZURE")
public class AzureAuthenticatedUserImpl implements AuthenticatedUser {

    @Value("${org.azbuilder.owner}")
    private String instanceOwner;

    @Autowired
    GroupService groupService;

    private AADOAuth2AuthenticatedPrincipal getAzureAdPrincipal(User user) {
        org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication bearerTokenAuthenticationToken = (org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication) user.getPrincipal();
        return (AADOAuth2AuthenticatedPrincipal) bearerTokenAuthenticationToken.getPrincipal();
    }

    @Override
    public String getEmail(User user) {
        return (String) getAzureAdPrincipal(user).getAttributes().get("unique_name");
    }

    @Override
    public String getApplication(User user) {
        log.info("oid {}",getAzureAdPrincipal(user).getAttributes().get("oid"));
        return (String) getAzureAdPrincipal(user).getAttributes().get("oid");
    }

    /**
     * Reference Azure AD Token Documentation https://docs.microsoft.com/en-us/azure/active-directory/develop/access-tokens
     * appidacr = 0 (public client authentication)
     * appidacr = 1 (client credentials authentication) will be considered as service account in Terrakube
     * appidacr = 2 (client certificate authentication)
     *
     * @param user
     * @return
     */
    @Override
    public boolean isServiceAccount(User user) {
        AADOAuth2AuthenticatedPrincipal aadoAuth2AuthenticatedPrincipal = getAzureAdPrincipal(user);
        return ((String) aadoAuth2AuthenticatedPrincipal.getAttributes().get("appidacr")).equals("1");
    }

    /**
     * Review is the authenticated user belongs to the Terrakube instance admin group.
     * @param user
     * @return
     */
    @Override
    public boolean isSuperUser(User user){
        boolean isServiceAccount=isServiceAccount(user);
        boolean isSuperUser;
        String applicationName="";
        String userName="";
        if (isServiceAccount){
            applicationName = getApplication(user);
            isSuperUser = groupService.isServiceMember(applicationName, instanceOwner);
        }else{
            userName = getEmail(user);
            isSuperUser = groupService.isMember(userName, instanceOwner);
        }

        log.info("{} is super user", isServiceAccount ? applicationName : userName);
        return isSuperUser;
    }
}
