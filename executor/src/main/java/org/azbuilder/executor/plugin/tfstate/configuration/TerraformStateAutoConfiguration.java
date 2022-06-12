package org.azbuilder.executor.plugin.tfstate.configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.client.TerrakubeClient;
import org.azbuilder.executor.plugin.tfstate.TerraformState;
import org.azbuilder.executor.plugin.tfstate.TerraformStatePathService;
import org.azbuilder.executor.plugin.tfstate.aws.AwsTerraformStateImpl;
import org.azbuilder.executor.plugin.tfstate.aws.AwsTerraformStateProperties;
import org.azbuilder.executor.plugin.tfstate.azure.AzureTerraformStateImpl;
import org.azbuilder.executor.plugin.tfstate.azure.AzureTerraformStateProperties;
import org.azbuilder.executor.plugin.tfstate.gcp.GcpTerraformStateImpl;
import org.azbuilder.executor.plugin.tfstate.gcp.GcpTerraformStateProperties;
import org.azbuilder.executor.plugin.tfstate.local.LocalTerraformStateImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

@Slf4j
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties({
        TerraformStateProperties.class,
        AzureTerraformStateProperties.class,
        AwsTerraformStateProperties.class,
        GcpTerraformStateProperties.class
})
@ConditionalOnMissingBean(TerraformState.class)
public class TerraformStateAutoConfiguration {

    @Bean
    public TerraformState terraformState(TerrakubeClient terrakubeClient, TerraformStateProperties terraformStateProperties, AzureTerraformStateProperties azureTerraformStateProperties, AwsTerraformStateProperties awsTerraformStateProperties, GcpTerraformStateProperties gcpTerraformStateProperties, TerraformStatePathService terraformStatePathService) {
        TerraformState terraformState = null;

        if (terraformStateProperties != null)
            switch (terraformStateProperties.getType()) {
                case AzureTerraformStateImpl:
                    BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                            .connectionString(
                                    String.format("DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s;EndpointSuffix=core.windows.net",
                                            azureTerraformStateProperties.getStorageAccountName(),
                                            azureTerraformStateProperties.getStorageAccessKey())
                            ).buildClient();

                    terraformState = AzureTerraformStateImpl.builder()
                            .resourceGroupName(azureTerraformStateProperties.getResourceGroupName())
                            .storageAccountName(azureTerraformStateProperties.getStorageAccountName())
                            .storageContainerName(azureTerraformStateProperties.getStorageContainerName())
                            .storageAccessKey(azureTerraformStateProperties.getStorageAccessKey())
                            .blobServiceClient(blobServiceClient)
                            .terrakubeClient(terrakubeClient)
                            .terraformStatePathService(terraformStatePathService)
                            .build();
                    break;
                case AwsTerraformStateImpl:
                    AWSCredentials credentials = new BasicAWSCredentials(
                            awsTerraformStateProperties.getAccessKey(),
                            awsTerraformStateProperties.getSecretKey()
                    );

                    AmazonS3 s3client = AmazonS3ClientBuilder
                            .standard()
                            .withCredentials(new AWSStaticCredentialsProvider(credentials))
                            .withRegion(Regions.fromName(awsTerraformStateProperties.getRegion()))
                            .build();

                    terraformState = AwsTerraformStateImpl.builder()
                            .s3client(s3client)
                            .bucketName(awsTerraformStateProperties.getBucketName())
                            .accessKey(awsTerraformStateProperties.getAccessKey())
                            .secretKey(awsTerraformStateProperties.getSecretKey())
                            .region(Regions.fromName(awsTerraformStateProperties.getRegion()))
                            .terrakubeClient(terrakubeClient)
                            .terraformStatePathService(terraformStatePathService)
                            .build();
                    break;
                case GcpTerraformStateImpl:
                    try {
                        Credentials gcpCredentials = GoogleCredentials.fromStream(new ByteArrayInputStream(
                                                Base64.getDecoder().decode(gcpTerraformStateProperties.getCredentials()))
                                );
                        Storage gcpStorage = StorageOptions.newBuilder()
                                .setCredentials(gcpCredentials)
                                .setProjectId(gcpTerraformStateProperties.getProjectId())
                                .build()
                                .getService();

                        terraformState = GcpTerraformStateImpl.builder().storage(gcpStorage)
                                .terraformStatePathService(terraformStatePathService)
                                .bucketName(gcpTerraformStateProperties.getBucketName())
                                .credentials(gcpTerraformStateProperties.getCredentials())
                                .terrakubeClient(terrakubeClient)
                                .build();
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                    break;
                default:
                    terraformState = LocalTerraformStateImpl.builder()
                            .terrakubeClient(terrakubeClient)
                            .build();
            }
        else
            terraformState = LocalTerraformStateImpl.builder()
                    .terrakubeClient(terrakubeClient)
                    .build();
        return terraformState;
    }
}
