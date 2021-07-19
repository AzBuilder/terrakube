package org.azbuilder.registry.plugin.storage.configuration;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.azbuilder.registry.plugin.storage.StorageService;
import org.azbuilder.registry.plugin.storage.azure.AzureStorageServiceImpl;
import org.azbuilder.registry.plugin.storage.azure.AzureStorageServiceProperties;
import org.azbuilder.registry.service.git.GitServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        AzureStorageServiceProperties.class,
        StorageProperties.class
})
@ConditionalOnMissingBean(StorageService.class)
public class StorageAutoConfiguration {

    @Bean
    public StorageService terraformOutput(StorageProperties storageProperties, AzureStorageServiceProperties azureStorageServiceProperties) {
        StorageService storageService = null;
        if (storageProperties != null)
            switch (storageProperties.getType()) {
                case AzureStorageImpl:
                    BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                            .connectionString(
                                    String.format("DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s;EndpointSuffix=core.windows.net",
                                            azureStorageServiceProperties.getAccountName(),
                                            azureStorageServiceProperties.getAccountKey())
                            ).buildClient();

                    storageService = AzureStorageServiceImpl.builder()
                            .blobServiceClient(blobServiceClient)
                            .gitService(new GitServiceImpl())
                            .build();
                    break;
                default:
                    storageService = null;
            }
        else
            storageService = null;
        return storageService;
    }
}
