package io.terrakube.registry.plugin.storage.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
@PropertySource(value = "classpath:application-${spring.profiles.active}.properties", ignoreResourceNotFound = true)
@ConfigurationProperties(prefix = "io.terrakube.registry.plugin.storage")
public class StorageProperties {
    private StorageType type;
}

enum StorageType {
    Local,
    AzureStorageImpl,
    AwsStorageImpl,
    GcpStorageImpl
}
