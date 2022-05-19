package org.azbuilder.api.plugin.storage.configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.storage.StorageTypeService;
import org.azbuilder.api.plugin.storage.aws.AwsStorageTypeProperties;
import org.azbuilder.api.plugin.storage.aws.AwsStorageTypeServiceImpl;
import org.azbuilder.api.plugin.storage.azure.AzureStorageTypeProperties;
import org.azbuilder.api.plugin.storage.azure.AzureStorageTypeServiceImpl;
import org.azbuilder.api.plugin.storage.local.LocalStorageTypeServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        AzureStorageTypeProperties.class,
        StorageTypeProperties.class
})
@ConditionalOnMissingBean(StorageTypeService.class)
@Slf4j
public class StorageTypeAutoConfiguration {

    @Bean
    public StorageTypeService terraformOutput(StorageTypeProperties storageTypeProperties, AzureStorageTypeProperties azureStorageTypeProperties, AwsStorageTypeProperties awsStorageTypeProperties) {
        StorageTypeService storageTypeService = null;
        log.info("StorageType={}", storageTypeProperties.getType());
        switch (storageTypeProperties.getType()) {
            case AZURE:
                BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                        .connectionString(
                                String.format("DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s;EndpointSuffix=core.windows.net",
                                        azureStorageTypeProperties.getAccountName(),
                                        azureStorageTypeProperties.getAccountKey())
                        ).buildClient();

                storageTypeService = AzureStorageTypeServiceImpl.builder()
                        .blobServiceClient(blobServiceClient)
                        .build();
                break;
            case AWS:
                AWSCredentials credentials = new BasicAWSCredentials(
                        awsStorageTypeProperties.getAccessKey(),
                        awsStorageTypeProperties.getSecretKey()
                );

                AmazonS3 s3client = AmazonS3ClientBuilder
                        .standard()
                        .withCredentials(new AWSStaticCredentialsProvider(credentials))
                        .withRegion(Regions.fromName(awsStorageTypeProperties.getRegion()))
                        .build();

                storageTypeService = AwsStorageTypeServiceImpl.builder()
                        .s3client(s3client)
                        .bucketName(awsStorageTypeProperties.getBucketName())
                        .build();
                break;
            default:
                storageTypeService = new LocalStorageTypeServiceImpl();
        }
        return storageTypeService;
    }
}
