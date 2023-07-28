package org.terrakube.api.plugin.datasource;

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
@ConfigurationProperties(prefix = "org.terrakube.api.plugin.datasource")
public class DataSourceConfigurationProperties {
    private DataSourceType type;
    private String hostname;
    private String databaseName;
    private String databaseUser;
    private String databasePassword;
    private String sslMode;
}