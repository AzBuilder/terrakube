package org.azbuilder.api.plugin.vcs.provider.bitbucket;

import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.vcs.provider.GetAccessToken;
import org.azbuilder.api.plugin.vcs.provider.exception.TokenException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Slf4j
@Service
public class BitbucketTokenService implements GetAccessToken<BitBucketToken> {

    @Override
    public BitBucketToken getAccessToken(String clientId, String clientSecret, String tempCode) throws TokenException {
        WebClient client = WebClient.builder()
                .baseUrl("https://bitbucket.org")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeaders(header -> header.setBasicAuth(clientId, clientSecret))
                .build();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("code", tempCode);

        BitBucketToken bitBucketToken = client.post()
                .uri("/site/oauth2/access_token")
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(BitBucketToken.class)
                .timeout(Duration.ofSeconds(10))
                .block();

        if(bitBucketToken != null) {
            return bitBucketToken;
        }
        else {
            throw new TokenException("500","Unable to get GitHub Token");
        }
    }

    public BitBucketToken refreshAccessToken(String clientId, String clientSecret, String refreshToken) throws TokenException {
        WebClient client = WebClient.builder()
                .baseUrl("https://bitbucket.org")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeaders(header -> header.setBasicAuth(clientId, clientSecret))
                .build();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("refresh_token", refreshToken);

        BitBucketToken bitBucketToken = client.post()
                .uri("/site/oauth2/access_token")
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(BitBucketToken.class)
                .block();

        if(bitBucketToken != null) {
            return bitBucketToken;
        }
        else {
            throw new TokenException("500","Unable to get GitHub Token");
        }
    }
}
