package io.terrakube.api.plugin.scheduler.module;

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
@ConfigurationProperties(prefix = "io.terrakube.api.module.cache")
public class CacheConfigurationProperties {

    private String maxTotal;
    private String maxIdle;
    private String minIdle;
    private String timeout;
    private String schedule;
}
