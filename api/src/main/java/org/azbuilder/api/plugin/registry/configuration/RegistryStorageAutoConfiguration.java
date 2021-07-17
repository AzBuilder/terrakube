package org.azbuilder.api.plugin.registry.configuration;

import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.registry.RegistryStorage;
import org.azbuilder.api.plugin.registry.provider.azure.AzureRegistryStorageImpl;
import org.azbuilder.api.plugin.registry.provider.azure.AzureRegistryStorageProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties({
        AzureRegistryStorageProperties.class
})
@ConditionalOnMissingBean(RegistryStorage.class)
public class RegistryStorageAutoConfiguration {

    @Bean
    public RegistryStorage registryStorage(RegistryStorageProperties registryStorageProperties) {
        log.info("DataSourceType: {}", registryStorageProperties.getType());
        RegistryStorage registryStorage = null;
        switch (registryStorageProperties.getType()) {
            case AZURE:
               registryStorage = new AzureRegistryStorageImpl();
                break;
            default:
                break;
        }
        return registryStorage;
    }
}
