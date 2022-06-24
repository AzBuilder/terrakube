package org.terrakube.executor.configuration;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.terrakube.executor.plugin.tfoutput.configuration.TerraformOutputProperties;
import org.terrakube.executor.plugin.tfoutput.configuration.TerraformOutputType;
import org.terrakube.executor.plugin.tfstate.configuration.TerraformStateProperties;
import org.terrakube.executor.plugin.tfstate.configuration.TerraformStateType;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AllArgsConstructor
@Slf4j
@Configuration
public class LocalConfiguration implements WebMvcConfigurer {

    TerraformOutputProperties terraformOutputProperties;
    TerraformStateProperties terraformStateProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("Output Enabled: {}", terraformOutputProperties.getType());
        log.info("State Enabled: {}", terraformStateProperties.getType());

        if (terraformOutputProperties.getType().equals(TerraformOutputType.LocalTerraformOutputImpl))
            registry.addResourceHandler("/output/**")
                    .addResourceLocations("file:" + FileUtils.getUserDirectoryPath() + "/.terraform-spring-boot/local/output/");

        if (terraformStateProperties.getType().equals(TerraformStateType.LocalTerraformStateImpl))
            registry.addResourceHandler("/state/**")
                    .addResourceLocations("file:" + FileUtils.getUserDirectoryPath() + "/.terraform-spring-boot/local/state/");


    }
}