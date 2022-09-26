package org.terrakube.api.plugin.storage.configuration;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
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
import org.apache.commons.codec.binary.Base64;
import org.terrakube.api.plugin.storage.StorageTypeService;
import org.terrakube.api.plugin.storage.aws.AwsStorageTypeProperties;
import org.terrakube.api.plugin.storage.aws.AwsStorageTypeServiceImpl;
import org.terrakube.api.plugin.storage.azure.AzureStorageTypeProperties;
import org.terrakube.api.plugin.storage.azure.AzureStorageTypeServiceImpl;
import org.terrakube.api.plugin.storage.gcp.GcpStorageTypeProperties;
import org.terrakube.api.plugin.storage.gcp.GcpStorageTypeServiceImpl;
import org.terrakube.api.plugin.storage.local.LocalStorageTypeServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Configuration
@EnableConfigurationProperties({
        AzureStorageTypeProperties.class,
        StorageTypeProperties.class,
        GcpStorageTypeProperties.class
})
@ConditionalOnMissingBean(StorageTypeService.class)
@Slf4j
public class StorageTypeAutoConfiguration {

    @Bean
    public StorageTypeService terraformOutput(StorageTypeProperties storageTypeProperties, AzureStorageTypeProperties azureStorageTypeProperties, AwsStorageTypeProperties awsStorageTypeProperties, GcpStorageTypeProperties gcpStorageTypeProperties) {
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

                AmazonS3 s3client = null;
                if (awsStorageTypeProperties.getEndpoint() != "" && awsStorageTypeProperties.getEndpoint() != "${AwsEndpoint}") {
                    ClientConfiguration clientConfiguration = new ClientConfiguration();
                    clientConfiguration.setSignerOverride("AWSS3V4SignerType");
                    
                    s3client = AmazonS3ClientBuilder
                            .standard()
                            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(awsStorageTypeProperties.getEndpoint(), awsStorageTypeProperties.getRegion()))
                            .withCredentials(new AWSStaticCredentialsProvider(credentials))
                            .withClientConfiguration(clientConfiguration)
                            .withPathStyleAccessEnabled(true)
                            .build();
                }else
                    s3client = AmazonS3ClientBuilder
                            .standard()
                            .withCredentials(new AWSStaticCredentialsProvider(credentials))
                            .withRegion(Regions.fromName(awsStorageTypeProperties.getRegion()))
                            .build();

                storageTypeService = AwsStorageTypeServiceImpl.builder()
                        .s3client(s3client)
                        .bucketName(awsStorageTypeProperties.getBucketName())
                        .build();
                break;
            case GCP:
                log.info("GCP Base64 {} length", gcpStorageTypeProperties.getCredentials().length());
                Credentials gcpCredentials = null;
                try {
                    gcpCredentials = GoogleCredentials.fromStream(
                            new ByteArrayInputStream(
                                    Base64.decodeBase64(gcpStorageTypeProperties.getCredentials())
                            )
                    );
                    Storage gcpStorage = StorageOptions.newBuilder()
                            .setCredentials(gcpCredentials)
                            .setProjectId(gcpStorageTypeProperties.getProjectId())
                            .build()
                            .getService();

                    storageTypeService = GcpStorageTypeServiceImpl.builder()
                            .storage(gcpStorage)
                            .bucketName(gcpStorageTypeProperties.getBucketName())
                            .build();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }

                break;
            default:
                storageTypeService = new LocalStorageTypeServiceImpl();
        }
        return storageTypeService;
    }
}
