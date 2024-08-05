package org.terrakube.api.plugin.vcs;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.List;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.terrakube.api.plugin.scheduler.ScheduleGitHubAppTokenService;
import org.terrakube.api.plugin.scheduler.ScheduleVcsService;
import org.terrakube.api.plugin.vcs.provider.github.GitHubAppTokenService;
import org.terrakube.api.rs.vcs.GitHubAppToken;
import org.terrakube.api.rs.vcs.Vcs;
import org.terrakube.api.rs.vcs.VcsType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
// A service/interface that tries to be the facet of all similar standalone
// token services// A service/interface that tries to be the facet of all
// similar standalone token services
public class StandAloneTokenService {

    @Autowired
    ScheduleVcsService scheduleVcsService;
    @Autowired
    GitHubAppTokenService gitHubAppTokenService;
    @Autowired
    ScheduleGitHubAppTokenService scheduleGitHubAppTokenService;

    public void generateAccessTokenTask(String vcsId) throws ParseException, SchedulerException {
        // Create a one-off task to fetch access tokens for exiting installations of this app.
        scheduleVcsService.createTask(vcsId);
    }

    public List<GitHubAppToken> refreshAccessToken(Vcs vcs)
            throws JsonMappingException, JsonProcessingException, NoSuchAlgorithmException, InvalidKeySpecException {
        if (vcs.getVcsType() != VcsType.GITHUB)
            return null;
        List<GitHubAppToken> gitHubAppTokens = gitHubAppTokenService.generateAccessToken(vcs);
        for (GitHubAppToken gitHubAppToken : gitHubAppTokens) {
            // Create a task to refresh the token every 55 minutes
            try {
                scheduleGitHubAppTokenService.createTask(3300 , gitHubAppToken.getId().toString());
                log.debug("Successfully created schedule task to refresh GitHub App token for owner/organization {}", gitHubAppToken.getOwner());
            } catch (SchedulerException e) {
                log.error("Failed to create schedule task to refresh GitHub App token for owner/organization {}, error {}", gitHubAppToken.getOwner(), e);
            }
        }
        return gitHubAppTokens;
    }

    // Refreshes the access token for all existing installations of the app
    public String refreshAccessToken(GitHubAppToken gitHubAppToken)
            throws JsonMappingException, JsonProcessingException, NoSuchAlgorithmException, InvalidKeySpecException {
        return gitHubAppTokenService.refreshAccessToken(gitHubAppToken);
    }

    // Generates a new access token for a new installation of the app, and also creates a task to refresh the token every 55 minutes
    public GitHubAppToken generateAccessToken(Vcs vcs, String[] ownerAndRepo) throws JsonMappingException,
            JsonProcessingException, NoSuchAlgorithmException, InvalidKeySpecException, SchedulerException {
        GitHubAppToken gitHubAppToken = gitHubAppTokenService.generateAccessToken(vcs, ownerAndRepo);
        // Create a task to refresh the token every 55 minutes
        scheduleGitHubAppTokenService.createTask(3300, gitHubAppToken.getId().toString());
        return gitHubAppToken;
    }
}
