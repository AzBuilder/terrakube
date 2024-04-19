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
        Instant now = Instant.now();
        String jwtToken = "";
        if (privateKeyPath != null && !privateKeyPath.isEmpty())
            try {
                jwtToken = Jwts.builder()
                        .setSubject(String.format("organization:%s:workspace:%s", job.getOrganization().getName(), job.getWorkspace().getName()))
                        .setAudience(workspaceEnvVariables.get("WORKLOAD_IDENTITY_AUDIENCE_AZURE"))
                        .setId(UUID.randomUUID().toString())
                        .setHeaderParam("kid", kid)
                        .claim("terrakube_workspace_id", job.getWorkspace().getId())
                        .claim("terrakube_organization_id", job.getOrganization().getId())
                        .claim("terrakube_job_id", String.valueOf(job.getId()))
                        .setIssuedAt(Date.from(now))
                        .setIssuer(String.format("https://%s", hostname))
                        .setExpiration(Date.from(now.plus(dynamicCredentialTtl, ChronoUnit.MINUTES)))
                        .signWith(getPrivateKey())
                        .compact();

                log.info("ARM_OIDC_TOKEN: {}", jwtToken);
                workspaceEnvVariables.put("ARM_OIDC_TOKEN", jwtToken);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        else {
            log.error("DynamicCredentialPrivateKeyPath not set, to generate Azure Dynamic Credentials the value is need it");
        }

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
