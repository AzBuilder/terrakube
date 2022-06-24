package org.terrakube.api.plugin.storage.configuration;

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
@ConfigurationProperties(prefix = "org.terrakube.storage")
public class StorageTypeProperties {
    private StorageTypeEnum type;
}

enum StorageTypeEnum {
    AWS,
    AZURE,
    GCP,
    LOCAL
}
