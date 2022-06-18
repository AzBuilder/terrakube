package org.terrakube.executor.plugin.tfoutput.configuration;


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
@ConfigurationProperties(prefix = "org.terrakube.executor.plugin.tfoutput")
public class TerraformOutputProperties {

    private TerraformOutputType type;
}

