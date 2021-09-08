package org.azbuilder.api.plugin.datasource.postgresql;

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
@ConfigurationProperties(prefix = "org.azbuilder.api.plugin.datasource.postgresql")
public class PostgreSQLDataSourceProperties {
    private String serverName;
    private String databaseName;
    private String databaseUser;
    private String databasePassword;
}
