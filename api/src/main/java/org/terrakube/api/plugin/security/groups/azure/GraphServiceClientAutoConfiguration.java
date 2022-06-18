package org.terrakube.api.plugin.security.groups.azure;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.requests.GraphServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "org.terrakube.api.groups", name = "type", havingValue = "AZURE")
public class GraphServiceClientAutoConfiguration {

    @Bean
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

    @Bean
    public Caffeine caffeineConfig() {
        return Caffeine.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES);
    }

    @Bean
    public CacheManager cacheManager(Caffeine caffeine) {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }
}
