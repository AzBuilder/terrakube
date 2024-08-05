package org.terrakube.api.plugin.vcs;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.terrakube.api.repository.GitHubAppTokenRepository;
import org.terrakube.api.repository.VcsRepository;
import org.terrakube.api.rs.vcs.Vcs;
import org.terrakube.api.rs.vcs.VcsConnectionType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class VcsTokenService {

    GitHubAppTokenRepository gitHubAppTokenRepository;
    StandAloneTokenService standAloneTokenService;
    VcsRepository vcsRepository;

    // Get the access token for access to the supplied repository, ownerAndRepo is
    // an array of the owner and the repository name
    public String getAccessToken(String[] ownerAndRepo, Vcs vcs) throws JsonMappingException, JsonProcessingException,
            NoSuchAlgorithmException, InvalidKeySpecException, SchedulerException {
        String token = vcs.getAccessToken();
        if (vcs.getConnectionType() == VcsConnectionType.STANDALONE) {
            token = gitHubAppTokenRepository.findByOwner(ownerAndRepo[0]).getToken();
            if (token == null) {
                token = standAloneTokenService.generateAccessToken(vcs, ownerAndRepo).getToken();
            }
        }

        return token;
    }

    // Get the access token for access to the supplied repository in full URL
    public String getAccessToken(String gitPath, Vcs vcs) throws URISyntaxException, JsonMappingException,
            JsonProcessingException, NoSuchAlgorithmException, InvalidKeySpecException, SchedulerException {
        URI uri = new URI(gitPath);
        String[] ownerAndRepo = Arrays.copyOfRange(uri.getPath().replaceAll("\\.git$", gitPath).split("/"), 1, 3);
        return getAccessToken(ownerAndRepo, vcs);
    }
}
