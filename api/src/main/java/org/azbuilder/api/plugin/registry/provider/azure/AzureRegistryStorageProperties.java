package org.azbuilder.api.plugin.registry.provider.azure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@PropertySources({
        @PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:application-${spring.profiles.active}.properties", ignoreResourceNotFound = true)
})
@ConfigurationProperties(prefix = "org.azbuilder.api.plugin.registry.provider.azure")
public class AzureRegistryStorageProperties {
    private String accountName;
    private String accountKey;
}
