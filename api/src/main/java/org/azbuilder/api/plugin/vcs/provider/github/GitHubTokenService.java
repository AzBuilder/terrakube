package org.azbuilder.api.plugin.vcs.provider.github;

import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.vcs.provider.GetAccessToken;
import org.azbuilder.api.plugin.vcs.provider.exception.TokenException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class GitHubTokenService implements GetAccessToken<GitHubToken> {
    public GitHubToken getAccessToken(String clientId, String clientSecret, String tempCode) throws TokenException {
        WebClient client = WebClient.builder()
                .baseUrl("https://github.com")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();

        GitHubToken gitHubToken = client.post().uri(uriBuilder -> uriBuilder.path("/login/oauth/access_token")
                        .queryParam("client_id", clientId)
                        .queryParam("client_secret", clientSecret)
                        .queryParam("code",tempCode)
                        .build())
                .retrieve().bodyToMono(GitHubToken.class).block();

        if(gitHubToken != null)
            return gitHubToken;
        else {
            throw new TokenException("500","Unable to get GitHub Token");
        }
    }
}
