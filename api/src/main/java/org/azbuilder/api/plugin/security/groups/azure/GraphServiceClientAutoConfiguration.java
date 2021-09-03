package org.azbuilder.api.plugin.security.groups.azure;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.requests.GraphServiceClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class GraphServiceClientAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "org.azbuilder.api.groups", name = "type", havingValue = "AzureAd")
    GraphServiceClient graphServiceClient(AzureAdGroupServiceProperties azureAdGroupServiceProperties){
        ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId(azureAdGroupServiceProperties.getClientId())
                .clientSecret(azureAdGroupServiceProperties.getSecret())
                .tenantId(azureAdGroupServiceProperties.getTenantId())
                .build();
        TokenCredentialAuthProvider tokenCredentialAuthProvider = new TokenCredentialAuthProvider(
                Arrays.asList("https://graph.microsoft.com/.default"), clientSecretCredential);
        return GraphServiceClient.builder().authenticationProvider(tokenCredentialAuthProvider).buildClient();
    }
}
