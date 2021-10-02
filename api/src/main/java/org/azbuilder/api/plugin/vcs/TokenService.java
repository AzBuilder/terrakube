package org.azbuilder.api.plugin.vcs;

import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.vcs.provider.bitbucket.BitBucketToken;
import org.azbuilder.api.plugin.vcs.provider.bitbucket.BitbucketTokenService;
import org.azbuilder.api.plugin.vcs.provider.exception.TokenException;
import org.azbuilder.api.plugin.vcs.provider.github.GitHubToken;
import org.azbuilder.api.plugin.vcs.provider.github.GitHubTokenService;
import org.azbuilder.api.repository.VcsRepository;
import org.azbuilder.api.rs.vcs.Vcs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public boolean generateAccessToken(String vcsId, String tempCode){
        Vcs vcs = vcsRepository.getOne(UUID.fromString(vcsId));
        try {
            switch (vcs.getVcsType()){
                case GITHUB:
                    GitHubToken gitHubToken = gitHubTokenService.getAccessToken(vcs.getClientId(),vcs.getClientSecret(),tempCode);
                    vcs.setAccessToken(gitHubToken.getAccess_token());
                    break;
                case BITBUCKET:
                    BitBucketToken bitBucketToken = bitbucketTokenService.getAccessToken(vcs.getClientId(),vcs.getClientSecret(), tempCode);
                    vcs.setAccessToken(bitBucketToken.getAccess_token());
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

}
