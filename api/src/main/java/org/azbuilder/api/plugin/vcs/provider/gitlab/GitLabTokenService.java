package org.azbuilder.api.plugin.vcs.provider.gitlab;

import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.vcs.provider.GetAccessToken;
import org.azbuilder.api.plugin.vcs.provider.bitbucket.BitBucketToken;
import org.azbuilder.api.plugin.vcs.provider.exception.TokenException;
import org.azbuilder.api.plugin.vcs.provider.github.GitHubToken;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class GitLabTokenService {

    public GitLabToken getAccessToken(String vcsId, String clientId, String clientSecret, String tempCode) throws TokenException{
        GitLabToken gitLabToken = getWebClient().post().uri(uriBuilder -> uriBuilder.path("/oauth/token")
                        .queryParam("client_id", clientId)
                        .queryParam("client_secret", clientSecret)
                        .queryParam("code",tempCode)
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("redirect_uri", "http://localhost:8080/callback/v1/vcs/".concat(vcsId))
                        .build())
                .retrieve().bodyToMono(GitLabToken.class).block();

        return validateNewToken(gitLabToken);
    }

    public GitLabToken refreshAccessToken(String vcsId, String clientId, String clientSecret, String refreshToken) throws TokenException {
        GitLabToken gitLabToken = getWebClient().post().uri(uriBuilder -> uriBuilder.path("/oauth/token")
                        .queryParam("client_id", clientId)
                        .queryParam("client_secret", clientSecret)
                        .queryParam("refresh_token", refreshToken)
                        .queryParam("grant_type", "refresh_token")
                        .queryParam("redirect_uri", "http://localhost:8080/callback/v1/vcs/".concat(vcsId))
                        .build())
                .retrieve().bodyToMono(GitLabToken.class).block();

        return validateNewToken(gitLabToken);
    }

    private WebClient getWebClient(){
        return WebClient.builder()
                .baseUrl("https://gitlab.com")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private GitLabToken validateNewToken(GitLabToken gitLabToken) throws TokenException {
        if(gitLabToken != null) {
            return gitLabToken;
        } else {
            throw new TokenException("500","Unable to get GitHub Token");
        }
    }
}
