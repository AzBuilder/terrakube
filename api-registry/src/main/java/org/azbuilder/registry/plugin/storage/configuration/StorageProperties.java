package org.azbuilder.registry.plugin.storage.configuration;

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
@ConfigurationProperties(prefix = "org.azbuilder.registry.plugin.storage")
public class StorageProperties {
    private StorageType type;
}

enum StorageType {
    AzureStorageImpl,
    AwsStorageImpl,
    GcpStorageImpl
}
