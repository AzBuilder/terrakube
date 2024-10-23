package org.terrakube.executor.plugin.tfoutput.configuration;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.extern.slf4j.Slf4j;
import org.terrakube.executor.plugin.tfoutput.TerraformOutput;
import org.terrakube.executor.plugin.tfoutput.TerraformOutputPathService;
import org.terrakube.executor.plugin.tfoutput.aws.AwsTerraformOutputImpl;
import org.terrakube.executor.plugin.tfoutput.aws.AwsTerraformOutputProperties;
import org.terrakube.executor.plugin.tfoutput.azure.AzureTerraformOutputImpl;
import org.terrakube.executor.plugin.tfoutput.azure.AzureTerraformOutputProperties;
import org.terrakube.executor.plugin.tfoutput.gcp.GcpTerraformOutputImpl;
import org.terrakube.executor.plugin.tfoutput.gcp.GcpTerraformOutputProperties;
import org.terrakube.executor.plugin.tfoutput.local.LocalTerraformOutputImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.endpoints.Endpoint;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.endpoints.S3EndpointParams;
import software.amazon.awssdk.services.s3.endpoints.S3EndpointProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Configuration
@EnableConfigurationProperties({
        AzureTerraformOutputProperties.class,
        AwsTerraformOutputProperties.class,
        GcpTerraformOutputProperties.class
})
@ConditionalOnMissingBean(TerraformOutput.class)
public class TerraformOutputAutoConfiguration {

    @Bean
    public TerraformOutput terraformOutput(TerraformOutputProperties terraformOutputProperties, AzureTerraformOutputProperties azureTerraformOutputProperties, AwsTerraformOutputProperties awsTerraformOutputProperties, GcpTerraformOutputProperties gcpTerraformOutputProperties, TerraformOutputPathService terraformOutputPathService) {
        TerraformOutput terraformOutput = null;

        if (terraformOutputProperties != null)
            switch (terraformOutputProperties.getType()) {
                case AzureTerraformOutputImpl:
                    BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                            .connectionString(
                                    String.format("DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s;EndpointSuffix=core.windows.net",
                                            azureTerraformOutputProperties.getAccountName(),
                                            azureTerraformOutputProperties.getAccountKey())
                            ).buildClient();

                    terraformOutput = AzureTerraformOutputImpl.builder()
                            .blobServiceClient(blobServiceClient)
                            .terraformOutputPathService(terraformOutputPathService)
                            .build();
                    break;
                case AwsTerraformOutputImpl:
                    S3Client s3client = null;
                    if (awsTerraformOutputProperties.getEndpoint() != null && !awsTerraformOutputProperties.getEndpoint().isEmpty()) {
                        log.info("Creating AWS SDK with custom endpoint and custom credentials");
                        s3client = S3Client.builder()
                                .region(Region.AWS_GLOBAL)
                                .credentialsProvider(StaticCredentialsProvider.create(getAwsBasicCredentials(awsTerraformOutputProperties)))
                                .endpointProvider(new S3EndpointProvider() {
                                    @Override
                                    public CompletableFuture<Endpoint> resolveEndpoint(S3EndpointParams endpointParams) {
                                        return CompletableFuture.completedFuture(Endpoint.builder()
                                                .url(URI.create(awsTerraformOutputProperties.getEndpoint() + "/" + endpointParams.bucket()))
                                                .build());
                                    }
                                })
                                .build();
                    } else {
                        if (awsTerraformOutputProperties.isEnableRoleAuthentication()) {
                            log.info("Creating AWS SDK with default credentials");
                            s3client = S3Client.builder()
                                    .region(Region.of(awsTerraformOutputProperties.getRegion()))
                                    .credentialsProvider(DefaultCredentialsProvider.create())
                                    .build();
                        } else {
                            log.info("Creating AWS SDK with custom credentials");
                            s3client = S3Client.builder()
                                    .region(Region.of(awsTerraformOutputProperties.getRegion()))
                                    .credentialsProvider(StaticCredentialsProvider.create(getAwsBasicCredentials(awsTerraformOutputProperties)))
                                    .build();
                        }
                    }

                    terraformOutput = AwsTerraformOutputImpl.builder()
                            .s3client(s3client)
                            .bucketName(awsTerraformOutputProperties.getBucketName())
                            .terraformOutputPathService(terraformOutputPathService)
                            .build();
                    break;
                case GcpTerraformOutputImpl:
                    try {
                        Credentials gcpCredentials = GoogleCredentials
                                .fromStream(
                                        new ByteArrayInputStream(
                                                Base64.getDecoder().decode(gcpTerraformOutputProperties.getCredentials()))
                                );
                        Storage gcpStorage = StorageOptions.newBuilder()
                                .setCredentials(gcpCredentials)
                                .setProjectId(gcpTerraformOutputProperties.getProjectId())
                                .build()
                                .getService();

                        terraformOutput = GcpTerraformOutputImpl.builder()
                                .terraformOutputPathService(terraformOutputPathService)
                                .bucketName(gcpTerraformOutputProperties.getBucketName())
                                .storage(gcpStorage)
                                .build();
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                    break;
                default:
                    terraformOutput = LocalTerraformOutputImpl.builder()
                            .terraformOutputPathService(terraformOutputPathService)
                            .build();
            }
        else
            terraformOutput = LocalTerraformOutputImpl.builder()
                    .terraformOutputPathService(terraformOutputPathService)
                    .build();
        return terraformOutput;
    }

    private AwsBasicCredentials getAwsBasicCredentials(AwsTerraformOutputProperties awsTerraformOutputProperties) {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(awsTerraformOutputProperties.getAccessKey(), awsTerraformOutputProperties.getSecretKey());
        return awsCreds;
    }
}
