package org.terrakube.executor.plugin.tfoutput.configuration;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

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
                    AWSStaticCredentialsProvider awsStaticCredentialsProvider = null;

                    if(awsTerraformOutputProperties.isEnableRoleAuthentication()) {
                        log.warn("Using aws role authentication");
                        AWSSecurityTokenService stsClient = AWSSecurityTokenServiceClientBuilder
                                .standard()
                                .withRegion(awsTerraformOutputProperties.getRegion())
                                .build();

                        AssumeRoleRequest roleRequest = new AssumeRoleRequest()
                                .withRoleArn(awsTerraformOutputProperties.getRoleArn())
                                .withRoleSessionName(awsTerraformOutputProperties.getRoleSessionName());

                        AssumeRoleResult assumeRoleResult = stsClient.assumeRole(roleRequest);

                        com.amazonaws.services.securitytoken.model.Credentials sessionCredentials = assumeRoleResult.getCredentials();

                        BasicSessionCredentials basicSessionCredentials = new BasicSessionCredentials(
                                sessionCredentials.getAccessKeyId(), sessionCredentials.getSecretAccessKey(),
                                sessionCredentials.getSessionToken());

                        awsStaticCredentialsProvider= new AWSStaticCredentialsProvider(basicSessionCredentials);

                    } else {
                        log.warn("Using aws access key and secret key for authentication");
                        AWSCredentials credentials = new BasicAWSCredentials(
                                awsTerraformOutputProperties.getAccessKey(),
                                awsTerraformOutputProperties.getSecretKey()
                        );
                        awsStaticCredentialsProvider = new AWSStaticCredentialsProvider(credentials);
                    }

                    AmazonS3 s3client = null;
                    if (awsTerraformOutputProperties.getEndpoint() != "") {
                        ClientConfiguration clientConfiguration = new ClientConfiguration();
                        clientConfiguration.setSignerOverride("AWSS3V4SignerType");

                        s3client = AmazonS3ClientBuilder
                                .standard()
                                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(awsTerraformOutputProperties.getEndpoint(), awsTerraformOutputProperties.getRegion()))
                                .withPathStyleAccessEnabled(true)
                                .withClientConfiguration(clientConfiguration)
                                .withCredentials(awsStaticCredentialsProvider)
                                .build();
                    } else
                        s3client = AmazonS3ClientBuilder
                                .standard()
                                .withCredentials(awsStaticCredentialsProvider)
                                .withRegion(Regions.fromName(awsTerraformOutputProperties.getRegion()))
                                .build();

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
}
