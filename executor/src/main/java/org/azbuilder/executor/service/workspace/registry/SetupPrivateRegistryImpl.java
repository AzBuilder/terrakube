package org.azbuilder.executor.service.workspace.registry;

import feign.Feign;
import feign.form.FormEncoder;
import feign.gson.GsonDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.azbuilder.api.client.security.azure.ClientCredentialApi;
import org.azbuilder.api.spring.autoconfigure.RestClientProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

@Slf4j
@Service
public class SetupPrivateRegistryImpl implements SetupPrivateRegistry {

    private static final String AZURE_ENDPOINT = "https://login.microsoftonline.com";
    private static final String GRANT_TYPE = "client_credentials";
    private static final String CREDENTIALS_FILE_NAME = "/.terraformrc";
    private static final String CREDENTIALS_CONTENT = "credentials \"%s\" {\n" +
            "token = \"%s\"" +
            "}";

    RestClientProperties clientProperties;
    String registryDomain;

    public SetupPrivateRegistryImpl(RestClientProperties restClientProperties, @Value("${org.terrakube.registry.domain}") String registryDomain) {
        this.clientProperties = restClientProperties;
        this.registryDomain = registryDomain;
    }

    @Override
    public void addCredentials(File workingDirectory) {
        ClientCredentialApi clientCredentialApi = Feign.builder()
                .encoder(new FormEncoder())
                .decoder(new GsonDecoder())
                .target(ClientCredentialApi.class, AZURE_ENDPOINT);

        Map<String, String> response = clientCredentialApi.getAccessToken(
                clientProperties.getTenantId(),
                GRANT_TYPE,
                clientProperties.getClientId(),
                clientProperties.getScope(),
                clientProperties.getClientSecret());

        String token = response.get("access_token");
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
