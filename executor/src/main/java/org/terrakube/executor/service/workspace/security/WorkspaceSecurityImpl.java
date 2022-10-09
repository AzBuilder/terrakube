package org.terrakube.executor.service.workspace.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.terrakube.client.spring.autoconfigure.RestClientProperties;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
public class WorkspaceSecurityImpl implements WorkspaceSecurity {

    private static final String ISSUER = "TerrakubeInternal";
    private static final String SUBJECT = "TerrakubeInternal (TOKEN)";
    private static final String EMAIL = "no-reply@terrakube.org";
    private static final String NAME = "TerrakubeInternal Client";
    private static final String CREDENTIALS_FILE_NAME = "/.terraformrc";
    private static final String CREDENTIALS_CONTENT = "credentials \"%s\" {\n" +
            "token = \"%s\"" +
            "}";

    RestClientProperties clientProperties;
    String registryDomain;

    String internalSecret;

    public WorkspaceSecurityImpl(RestClientProperties restClientProperties, @Value("${org.terrakube.registry.domain}") String registryDomain, @Value("${org.terrakube.client.secretKey}") String internalSecret) {
        this.clientProperties = restClientProperties;
        this.registryDomain = registryDomain;
        this.internalSecret = internalSecret;
    }

    @Override
    public String generateAccessToken() {
        log.error("Generate Dex Authentication Private Token");
        String newToken = "";

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(this.internalSecret));

        newToken = Jwts.builder()
                .setIssuer(WorkspaceSecurityImpl.ISSUER)
                .setSubject(WorkspaceSecurityImpl.SUBJECT)
                .setAudience(WorkspaceSecurityImpl.ISSUER)
                .claim("email", WorkspaceSecurityImpl.EMAIL)
                .claim("email_verified", true)
                .claim("name", WorkspaceSecurityImpl.NAME)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(30, ChronoUnit.DAYS)))
                .signWith(key)
                .compact();

        return newToken;
    }

    @Override
    public String generateAccessToken(int minutes) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(this.internalSecret));

        return Jwts.builder()
                .setIssuer(WorkspaceSecurityImpl.ISSUER)
                .setSubject(WorkspaceSecurityImpl.SUBJECT)
                .setAudience(WorkspaceSecurityImpl.ISSUER)
                .claim("email", WorkspaceSecurityImpl.EMAIL)
                .claim("email_verified", true)
                .claim("name", WorkspaceSecurityImpl.NAME)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(minutes, ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }

    @Override
    public void addTerraformCredentials(File workingDirectory) {

        String token = generateAccessToken();
        String credentialFileContent = String.format(CREDENTIALS_CONTENT, registryDomain, token);

        try {
            File credentialFile = new File(
                    FilenameUtils.separatorsToSystem(
                            FileUtils.getUserDirectoryPath().concat(CREDENTIALS_FILE_NAME)
                    )
            );
            synchronized (this) {
                FileUtils.writeStringToFile(credentialFile, credentialFileContent, Charset.defaultCharset(), false);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
