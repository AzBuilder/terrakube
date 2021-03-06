package org.terrakube.executor.service.workspace.registry;

import feign.Feign;
import feign.form.FormEncoder;
import feign.gson.GsonDecoder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.terrakube.client.dex.DexCredentialAuthentication;
import org.terrakube.client.dex.DexCredentialType;
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
public class SetupPrivateRegistryImpl implements SetupPrivateRegistry {

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

    public SetupPrivateRegistryImpl(RestClientProperties restClientProperties, @Value("${org.terrakube.registry.domain}") String registryDomain, @Value("${org.terrakube.client.secretKey}") String internalSecret) {
        this.clientProperties = restClientProperties;
        this.registryDomain = registryDomain;
        this.internalSecret = internalSecret;
    }

    private String generateAccessToken() {
        log.error("Generate Dex Authentication Private Token");
        String newToken = "";

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(this.internalSecret));

        newToken = Jwts.builder()
                .setIssuer(SetupPrivateRegistryImpl.ISSUER)
                .setSubject(SetupPrivateRegistryImpl.SUBJECT)
                .setAudience(SetupPrivateRegistryImpl.ISSUER)
                .claim("email", SetupPrivateRegistryImpl.EMAIL)
                .claim("email_verified", true)
                .claim("name", SetupPrivateRegistryImpl.NAME)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(30, ChronoUnit.DAYS)))
                .signWith(key)
                .compact();

        return newToken;
    }

    @Override
    public void addCredentials(File workingDirectory) {

        String token = generateAccessToken();
        String credentialFileContent = String.format(CREDENTIALS_CONTENT, registryDomain, token);

        try {
            File credentialFile = new File(
                    FilenameUtils.separatorsToSystem(
                            FileUtils.getUserDirectoryPath().concat(CREDENTIALS_FILE_NAME)
                    )
            );
            FileUtils.writeStringToFile(credentialFile, credentialFileContent, Charset.defaultCharset(), false);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
