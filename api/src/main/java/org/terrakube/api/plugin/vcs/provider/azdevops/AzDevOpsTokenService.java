package org.terrakube.api.plugin.vcs.provider.azdevops;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.terrakube.api.plugin.token.dynamic.DynamicCredentialsService;
import org.terrakube.api.plugin.vcs.provider.exception.TokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;

@Service
@Slf4j
public class AzDevOpsTokenService {

    @Value("${org.terrakube.hostname}")
    private String hostname;

    @Autowired
    private DynamicCredentialsService dynamicCredentialsService;

    private static final String DEFAULT_ENDPOINT="https://app.vssps.visualstudio.com";
    private static final String AZURE_DEVOPS_SCOPE = "499b84ac-1321-427f-aa17-267ca6975798/.default"; // Azure DevOps scope


    public AzDevOpsToken getAccessToken(String vcsId, String clientSecret, String tempCode, String callback, String endpoint) throws TokenException {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
        formData.add("client_assertion", clientSecret);
        formData.add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");
        formData.add("assertion", tempCode);
        formData.add("redirect_uri", String.format("https://%s/callback/v1/vcs/%s", hostname, callback == null ? vcsId: callback));

        AzDevOpsToken azDevOpsToken = getWebClient(endpoint).post()
                .uri("/oauth2/token")
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(AzDevOpsToken.class)
                .block();

        return validateNewToken(azDevOpsToken);
    }

    public AzDevOpsToken refreshAccessToken(String vcsId, String clientSecret, String refreshToken, String callback, String endpoint) throws TokenException {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
        formData.add("client_assertion", clientSecret);
        formData.add("grant_type", "refresh_token");
        formData.add("assertion", refreshToken);
        formData.add("redirect_uri", String.format("https://%s/callback/v1/vcs/%s", hostname, callback == null ? vcsId: callback));

        AzDevOpsToken azDevOpsToken  = getWebClient(endpoint).post()
                .uri("/oauth2/token")
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(AzDevOpsToken.class)
                .block();

        return validateNewToken(azDevOpsToken);
    }

    public AzDevOpsToken getAzureDefaultToken() throws TokenException {
        AzDevOpsToken azDevOpsToken = new AzDevOpsToken();
        try {
            DefaultAzureCredential credential = new DefaultAzureCredentialBuilder().build();
            TokenRequestContext requestContext = new TokenRequestContext()
                    .setScopes(Collections.singletonList(AZURE_DEVOPS_SCOPE));
            AccessToken accessToken = credential.getToken(requestContext).block();
            azDevOpsToken.setAccess_token(accessToken.getToken());
            log.debug("Azure Default Token: {}", azDevOpsToken.getAccess_token());
        } catch (Exception ex) {
            log.error("Error getting Azure Default Token: {}", ex.getMessage());
        }

        azDevOpsToken.setRefresh_token("n/a");
        azDevOpsToken.setToken_type("azure");
        azDevOpsToken.setExpires_in(3600);
        return validateNewToken(azDevOpsToken);
    }



    private WebClient getWebClient(String endpoint){
        return WebClient.builder()
                .baseUrl((endpoint != null)? endpoint : DEFAULT_ENDPOINT)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
    }

    private AzDevOpsToken validateNewToken(AzDevOpsToken azDevOpsToken) throws TokenException {
        if(azDevOpsToken != null) {
            return azDevOpsToken;
        } else {
            throw new TokenException("500","Unable to get Azure DevOps Token");
        }
    }
}
