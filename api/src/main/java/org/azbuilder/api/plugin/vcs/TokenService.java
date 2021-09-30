package org.azbuilder.api.plugin.vcs;

import lombok.extern.slf4j.Slf4j;
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

    public boolean setGitHubToken(String vcsId, String tempCode){
        Vcs vcs = vcsRepository.getOne(UUID.fromString(vcsId));
        vcs.setAccessToken(gitHubTokenService.getAccessToken(vcs.getClientId(),vcs.getClientSecret(),tempCode));
        vcsRepository.save(vcs);
        return true;
    }
}
