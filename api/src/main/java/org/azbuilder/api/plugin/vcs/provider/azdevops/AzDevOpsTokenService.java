package org.azbuilder.api.plugin.vcs.provider.azdevops;

import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.vcs.provider.exception.TokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class AzDevOpsTokenService {

    @Value("${org.terrakube.hostname}")
    private String hostname;

    public AzDevOpsToken getAccessToken(String vcsId, String clientSecret, String tempCode) throws TokenException {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
        formData.add("client_assertion", clientSecret);
        formData.add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");
        formData.add("assertion", tempCode);
        formData.add("redirect_uri", String.format("https://%s/%s", hostname, vcsId));

        AzDevOpsToken azDevOpsToken = getWebClient().post()
                .uri("/oauth2/token")
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(AzDevOpsToken.class)
                .block();

        return validateNewToken(azDevOpsToken);
    }

    public AzDevOpsToken refreshAccessToken(String vcsId, String clientSecret, String refreshToken) throws TokenException {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
        formData.add("client_assertion", clientSecret);
        formData.add("grant_type", "refresh_token");
        formData.add("assertion", refreshToken);
        formData.add("redirect_uri", String.format("https://%s/%s", hostname, vcsId));

        AzDevOpsToken azDevOpsToken  = getWebClient().post()
                .uri("/oauth2/token")
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(AzDevOpsToken.class)
                .block();

        return validateNewToken(azDevOpsToken);
    }

    private WebClient getWebClient(){
        return WebClient.builder()
                .baseUrl("https://app.vssps.visualstudio.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
    }

    private AzDevOpsToken validateNewToken(AzDevOpsToken azDevOpsToken) throws TokenException {
        if(azDevOpsToken != null) {
            log.info("Azure DevOps Token {}", azDevOpsToken.getAccess_token());
            return azDevOpsToken;
        } else {
            throw new TokenException("500","Unable to get GitHub Token");
        }
    }
}
