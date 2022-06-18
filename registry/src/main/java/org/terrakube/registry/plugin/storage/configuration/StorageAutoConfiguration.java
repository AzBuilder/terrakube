package org.terrakube.registry.plugin.storage.configuration;

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
import lombok.extern.slf4j.Slf4j;
import org.terrakube.registry.configuration.OpenRegistryProperties;
import org.terrakube.registry.plugin.storage.StorageService;
import org.terrakube.registry.plugin.storage.aws.AwsStorageServiceImpl;
import org.terrakube.registry.plugin.storage.aws.AwsStorageServiceProperties;
import org.terrakube.registry.plugin.storage.azure.AzureStorageServiceImpl;
import org.terrakube.registry.plugin.storage.azure.AzureStorageServiceProperties;
import org.terrakube.registry.plugin.storage.gcp.GcpStorageServiceImpl;
import org.terrakube.registry.plugin.storage.gcp.GcpStorageServiceProperties;
import org.terrakube.registry.plugin.storage.local.LocalStorageServiceImpl;
import org.terrakube.registry.service.git.GitServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

@Configuration
@EnableConfigurationProperties({
        AzureStorageServiceProperties.class,
        StorageProperties.class,
        OpenRegistryProperties.class,
        AwsStorageServiceProperties.class,
        GcpStorageServiceProperties.class
})
@ConditionalOnMissingBean(StorageService.class)
@Slf4j
public class StorageAutoConfiguration {

    @Bean
    public StorageService terraformOutput(OpenRegistryProperties openRegistryProperties, StorageProperties storageProperties, AzureStorageServiceProperties azureStorageServiceProperties, AwsStorageServiceProperties awsStorageServiceProperties, GcpStorageServiceProperties gcpStorageServiceProperties) {
        StorageService storageService = null;
        log.info("StorageType={}", storageProperties.getType());
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
                        .registryHostname(openRegistryProperties.getHostname())
                        .build();
                break;
            case AwsStorageImpl:

                AWSCredentials credentials = new BasicAWSCredentials(
                        awsStorageServiceProperties.getAccessKey(),
                        awsStorageServiceProperties.getSecretKey()
                );

                AmazonS3 s3client = AmazonS3ClientBuilder
                        .standard()
                        .withCredentials(new AWSStaticCredentialsProvider(credentials))
                        .withRegion(Regions.fromName(awsStorageServiceProperties.getRegion()))
                        .build();

                storageService = AwsStorageServiceImpl.builder()
                        .s3client(s3client)
                        .gitService(new GitServiceImpl())
                        .bucketName(awsStorageServiceProperties.getBucketName())
                        .registryHostname(openRegistryProperties.getHostname())
                        .build();
                break;
            case GcpStorageImpl:
                Credentials gcpCredentials = null;
                try {
                    log.info("Credentials Lenght: {}", gcpStorageServiceProperties.getCredentials().length());
                    log.info("GCP Project: {}", gcpStorageServiceProperties.getProjectId());
                    log.info("GCP Bucket: {}", gcpStorageServiceProperties.getBucketName());

                    gcpCredentials = GoogleCredentials
                            .fromStream(
                                    new ByteArrayInputStream(
                                            Base64.getDecoder().decode(gcpStorageServiceProperties.getCredentials()))
                            );
                    Storage gcpStorage = StorageOptions.newBuilder()
                            .setCredentials(gcpCredentials)
                            .setProjectId(gcpStorageServiceProperties.getProjectId())
                            .build()
                            .getService();

                    log.info("GCP Storage null: {}", gcpStorage == null);
                    storageService = GcpStorageServiceImpl.builder()
                            .bucketName(gcpStorageServiceProperties.getBucketName())
                            .storage(gcpStorage)
                            .gitService(new GitServiceImpl())
                            .registryHostname(openRegistryProperties.getHostname())
                            .build();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }

                break;
            case Local:
                storageService = new LocalStorageServiceImpl();
                break;
            default:
                storageService = null;
        }
        return storageService;
    }
}
