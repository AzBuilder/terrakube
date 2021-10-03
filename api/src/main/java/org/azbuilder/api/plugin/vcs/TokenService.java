package org.azbuilder.api.plugin.vcs;

import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.vcs.provider.bitbucket.BitBucketToken;
import org.azbuilder.api.plugin.vcs.provider.bitbucket.BitbucketTokenService;
import org.azbuilder.api.plugin.vcs.provider.exception.TokenException;
import org.azbuilder.api.plugin.vcs.provider.github.GitHubToken;
import org.azbuilder.api.plugin.vcs.provider.github.GitHubTokenService;
import org.azbuilder.api.plugin.vcs.provider.gitlab.GitLabToken;
import org.azbuilder.api.plugin.vcs.provider.gitlab.GitLabTokenService;
import org.azbuilder.api.repository.VcsRepository;
import org.azbuilder.api.rs.vcs.Vcs;
import org.azbuilder.api.rs.vcs.VcsType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class TokenService {

    @Autowired
    VcsRepository vcsRepository;

    @Autowired
    GitHubTokenService gitHubTokenService;

    @Autowired
    BitbucketTokenService bitbucketTokenService;

    @Autowired
    GitLabTokenService gitLabTokenService;

    public boolean generateAccessToken(String vcsId, String tempCode) {
        Vcs vcs = vcsRepository.getOne(UUID.fromString(vcsId));
        try {
            switch (vcs.getVcsType()) {
                case GITHUB:
                    GitHubToken gitHubToken = gitHubTokenService.getAccessToken(vcs.getClientId(), vcs.getClientSecret(), tempCode);
                    vcs.setAccessToken(gitHubToken.getAccess_token());
                    break;
                case BITBUCKET:
                    BitBucketToken bitBucketToken = bitbucketTokenService.getAccessToken(vcs.getClientId(), vcs.getClientSecret(), tempCode);
                    vcs.setAccessToken(bitBucketToken.getAccess_token());
                    vcs.setRefreshToken(bitBucketToken.getRefresh_token());
                    vcs.setTokenExpiration(new Date(System.currentTimeMillis() + bitBucketToken.getExpires_in() * 1000));
                    break;
                case GITLAB:
                    GitLabToken gitLabToken = gitLabTokenService.getAccessToken(vcs.getId().toString(), vcs.getClientId(), vcs.getClientSecret(), tempCode);
                    vcs.setAccessToken(gitLabToken.getAccess_token());
                    vcs.setRefreshToken(gitLabToken.getRefresh_token());
                    vcs.setTokenExpiration(new Date(System.currentTimeMillis() + gitLabToken.getExpires_in() * 1000));
                    break;
                default:
                    break;
            }

            vcsRepository.save(vcs);
        } catch (TokenException e) {
            log.error(e.getMessage());
        }

        return true;
    }

    public Map refreshAccessToken(String vcsId, VcsType vcsType, Date tokenExpiration, String clientId, String clientSecret, String refreshToken, String currentToken) {
        Map<String, Object> tokenInformation = new HashMap();
        switch (vcsType) {
            case BITBUCKET:
                //Refresh token every 1.5 hours, Bitbucket Token expire after 2 hours (7200 seconds)
                if (tokenExpiration != null && tokenExpiration.before(new Date(System.currentTimeMillis() + 5400 * 1000))) {
                    try {
                        BitBucketToken bitBucketToken = bitbucketTokenService.refreshAccessToken(clientId, clientSecret, refreshToken);
                        tokenInformation.put("accessToken", bitBucketToken.getAccess_token());
                        tokenInformation.put("refreshToken", bitBucketToken.getRefresh_token());
                        tokenInformation.put("tokenExpiration", new Date(System.currentTimeMillis() + bitBucketToken.getExpires_in() * 1000));
                    } catch (TokenException e) {
                        log.error(e.getMessage());
                    }
                }
                break;
            case GITLAB:
                //Refresh token every 1.5 hours, GitLab Token expire after 2 hours (7200 seconds)
                if (tokenExpiration != null && tokenExpiration.before(new Date(System.currentTimeMillis() + 5400 * 1000))) {
                    try {
                        GitLabToken gitLabToken = gitLabTokenService.refreshAccessToken(vcsId, clientId, clientSecret, refreshToken);
                        tokenInformation.put("accessToken", gitLabToken.getAccess_token());
                        tokenInformation.put("refreshToken", gitLabToken.getRefresh_token());
                        tokenInformation.put("tokenExpiration", new Date(System.currentTimeMillis() + gitLabToken.getExpires_in() * 1000));
                    } catch (TokenException e) {
                        log.error(e.getMessage());
                    }
                }
                break;
            default:
                break;
        }
        return tokenInformation;
    }

}
