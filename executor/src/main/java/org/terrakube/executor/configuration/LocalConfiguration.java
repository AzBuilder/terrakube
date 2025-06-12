package org.terrakube.executor.configuration;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.terrakube.executor.plugin.tfstate.configuration.TerraformStateProperties;
import org.terrakube.executor.plugin.tfstate.configuration.TerraformStateType;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AllArgsConstructor
@Slf4j
@Configuration
public class LocalConfiguration implements WebMvcConfigurer {

    TerraformStateProperties terraformStateProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("State/Output Enabled: {}", terraformStateProperties.getType());

        if (terraformStateProperties.getType().equals(TerraformStateType.LocalTerraformStateImpl))
            registry.addResourceHandler("/output/**")
                    .addResourceLocations("file:" + FileUtils.getUserDirectoryPath() + "/.terraform-spring-boot/local/output/");

        if (terraformStateProperties.getType().equals(TerraformStateType.LocalTerraformStateImpl))
            registry.addResourceHandler("/state/**")
                    .addResourceLocations("file:" + FileUtils.getUserDirectoryPath() + "/.terraform-spring-boot/local/state/");


    }
}