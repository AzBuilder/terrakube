package org.terrakube.executor.plugin.tfstate.azure;


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
@ConfigurationProperties(prefix = "org.terrakube.executor.plugin.tfstate.azure")
public class AzureTerraformStateProperties {

    private String resourceGroupName;
    private String storageAccountName;
    private String storageContainerName;
    private String storageAccessKey;
}
