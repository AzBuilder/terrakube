package org.terrakube.api.plugin.vcs.provider.github;

import org.terrakube.api.plugin.vcs.provider.GetAccessToken;
import org.terrakube.api.plugin.vcs.provider.exception.TokenException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GitHubTokenService implements GetAccessToken<GitHubToken> {

    private static final String DEFAULT_ENDPOINT="https://github.com";

    public GitHubToken getAccessToken(String clientId, String clientSecret, String tempCode, String callback, String endpoint) throws TokenException {
        WebClient client = WebClient.builder()
                .baseUrl((endpoint != null)? endpoint : DEFAULT_ENDPOINT)
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
