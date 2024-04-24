package org.terrakube.api.plugin.token.dynamic;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terrakube.api.rs.job.Job;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@Service
public class DynamicCredentialsService {

    @Value("${org.terrakube.hostname}")
    String hostname;

    @Value("${org.terrakube.dynamic.credentials.public-key-path}")
    String publicKeyPath;

    @Value("${org.terrakube.dynamic.credentials.private-key-path}")
    String privateKeyPath;

    @Value("${org.terrakube.dynamic.credentials.kid}")
    String kid;

    @Value("${org.terrakube.dynamic.credentials.ttl}")
    int dynamicCredentialTtl;

    @Transactional
    public HashMap<String, String> generateDynamicCredentialsAzure(Job job, HashMap<String, String> workspaceEnvVariables) {
        String jwtToken = generateJwt(
                job.getOrganization().getName(),
                job.getWorkspace().getName(),
                workspaceEnvVariables.get("WORKLOAD_IDENTITY_AUDIENCE_AZURE"),
                job.getOrganization().getId().toString(),
                job.getWorkspace().getId().toString(),
                job.getId()
        );

        log.info("ARM_OIDC_TOKEN: {}", jwtToken);
        workspaceEnvVariables.put("ARM_OIDC_TOKEN", jwtToken);

        return workspaceEnvVariables;
    }

    private String generateJwt(String organizationName, String workspaceName, String tokenAudience, String organizationId, String workspaceId, int jobId) {
        String jwtToken = "";
        if (privateKeyPath != null && !privateKeyPath.isEmpty()) {
            try {
                Instant now = Instant.now();
                jwtToken = Jwts.builder()
                        .setSubject(String.format("organization:%s:workspace:%s", organizationName, workspaceName))
                        .setAudience(tokenAudience)
                        .setId(UUID.randomUUID().toString())
                        .setHeaderParam("kid", kid)
                        .claim("terrakube_workspace_id", organizationId)
                        .claim("terrakube_organization_id", workspaceId)
                        .claim("terrakube_job_id", String.valueOf(jobId))
                        .setIssuedAt(Date.from(now))
                        .setIssuer(String.format("https://%s", hostname))
                        .setExpiration(Date.from(now.plus(dynamicCredentialTtl, ChronoUnit.MINUTES)))
                        .signWith(getPrivateKey())
                        .compact();

            } catch (Exception e) {
                log.error(e.getMessage());
            }
        } else {
            log.error("DynamicCredentialPrivateKeyPath not set, to generate Dynamic Credentials the value is need it");
        }

        return jwtToken;
    }

    @Transactional
    public HashMap<String, String> generateDynamicCredentialsAws(Job job, HashMap<String, String> workspaceEnvVariables) {
        log.warn("AWS Dynamic Credentials not implemented yet");
        return workspaceEnvVariables;
    }

    @Transactional
    public HashMap<String, String> generateDynamicCredentialsGcp(Job job, HashMap<String, String> workspaceEnvVariables) {
        String jwtToken = generateJwt(
                job.getOrganization().getName(),
                job.getWorkspace().getName(),
                workspaceEnvVariables.get("WORKLOAD_IDENTITY_AUDIENCE_GCP"),
                job.getOrganization().getId().toString(),
                job.getWorkspace().getId().toString(),
                job.getId()
        );

        String googleCredentialsFile = "{\n" +
                "    \"access_token\": \"%s\"\n" +
                "} ";

        googleCredentialsFile = String.format(googleCredentialsFile, jwtToken);

        String googleCredentialConfigFile = "{\n" +
                "    \"type\": \"external_account\",\n" +
                "    \"audience\": \"%s\",\n" +
                "    \"subject_token_type\": \"urn:ietf:params:oauth:token-type:jwt\",\n" +
                "    \"token_url\": \"https://sts.googleapis.com/v1/token\",\n" +
                "    \"service_account_impersonation_url\": \"https://iamcredentials.googleapis.com/v1/projects/-/serviceAccounts/%s:generateAccessToken\",\n" +
                "    \"credential_source\": {\n" +
                "      \"file\": \"%s/terrakube_dynamic_credentials.json\",\n" +
                "      \"format\": {\n" +
                "        \"type\": \"json\",\n" +
                "        \"subject_token_field_name\": \"access_token\"\n" +
                "      }\n" +
                "    }\n" +
                "  }";

        String executorDirectory = String.format(
                "%s/.terraform-spring-boot/executor/%s/%s",
                FileUtils.getUserDirectoryPath(),
                job.getOrganization().getId().toString(),
                job.getWorkspace().getId().toString()
        );

        String audience = workspaceEnvVariables.get("WORKLOAD_IDENTITY_AUDIENCE_GCP");
        String serviceAccountEmail = workspaceEnvVariables.get("WORKLOAD_IDENTITY_SERVICE_ACCOUNT_EMAIL");

        googleCredentialConfigFile = String.format(googleCredentialConfigFile, audience, serviceAccountEmail, executorDirectory);

        log.info("TERRAKUBE_GCP_CREDENTIALS_FILE: {}", googleCredentialsFile);
        log.info("TERRAKUBE_GCP_CREDENTIALS_CONFIG_FILE: {}", googleCredentialConfigFile);

        workspaceEnvVariables.put("TERRAKUBE_GCP_CREDENTIALS_FILE", googleCredentialsFile);
        workspaceEnvVariables.put("TERRAKUBE_GCP_CREDENTIALS_CONFIG_FILE", googleCredentialConfigFile);
        workspaceEnvVariables.put("GOOGLE_APPLICATION_CREDENTIALS", executorDirectory + "/terrakube_config_dynamic_credentials.json");

        return workspaceEnvVariables;
    }

    private PrivateKey getPrivateKey() throws Exception {
        String rsaPrivateKey = FileUtils.readFileToString(new File(privateKeyPath), StandardCharsets.UTF_8);

        rsaPrivateKey = rsaPrivateKey.replace("-----BEGIN PRIVATE KEY-----", "");
        rsaPrivateKey = rsaPrivateKey.replace("-----END PRIVATE KEY-----", "");

        String privateKeyPEMFinal = "";
        String line;
        BufferedReader bufReader = new BufferedReader(new StringReader(rsaPrivateKey));
        while ((line = bufReader.readLine()) != null) {
            privateKeyPEMFinal += line;
        }
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyPEMFinal));
        KeyFactory kf = KeyFactory.getInstance("RSA");

        return kf.generatePrivate(keySpec);
    }

    public String getPublicKey() {
        String publicKeyPEM = "";
        try {
            publicKeyPEM = FileUtils.readFileToString(new File(publicKeyPath), StandardCharsets.UTF_8);

            publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "");
            publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");

            String publicKeyPEMFinal = "";
            String line;
            BufferedReader bufReader = new BufferedReader(new StringReader(publicKeyPEM));
            while ((line = bufReader.readLine()) != null) {
                publicKeyPEMFinal += line;
            }

            log.info("Dynamic Credentials Public Key: {}", publicKeyPEMFinal);
            return publicKeyPEMFinal;
        } catch (Exception ex) {
            publicKeyPEM = "";
            log.error(ex.getMessage());
        }

        return publicKeyPEM;
    }
}
