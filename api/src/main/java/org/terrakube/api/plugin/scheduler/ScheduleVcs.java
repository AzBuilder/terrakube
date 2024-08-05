package org.terrakube.api.plugin.scheduler;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.terrakube.api.plugin.vcs.StandAloneTokenService;
import org.terrakube.api.plugin.vcs.TokenService;
import org.terrakube.api.repository.GitHubAppTokenRepository;
import org.terrakube.api.repository.VcsRepository;
import org.terrakube.api.rs.vcs.GitHubAppToken;
import org.terrakube.api.rs.vcs.Vcs;
import org.terrakube.api.rs.vcs.VcsConnectionType;
import org.terrakube.api.rs.vcs.VcsStatus;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Component
@Getter
@Setter
@Slf4j
public class ScheduleVcs implements org.quartz.Job {

    public static final String VCS_ID = "vcsId";

    TokenService tokenService;
    VcsRepository vcsRepository;
    GitHubAppTokenRepository gitHubAppTokenRepository;
    StandAloneTokenService standAloneTokenService;
    ScheduleGitHubAppTokenService scheduleGitHubAppTokenService;

    @Transactional
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String vcsId = jobExecutionContext.getJobDetail().getJobDataMap().getString(VCS_ID);
        Vcs vcs = null;
        Optional<Vcs> search = vcsRepository.findById(UUID.fromString(vcsId));
        if (search.isPresent()) {
            log.info("VCS connection found using Id");
            vcs = search.get();
        } else {
            vcs = vcsRepository.findByCallback(vcsId);
            if (vcs == null) {
                log.warn(
                        "VCS Job Id {} is still active but no longer needed it, vcs connection cannot be found in the database",
                        vcsId);
                return;
            }
            log.info("VCS found with custom callback");
        }

        if (vcs.getConnectionType() == VcsConnectionType.STANDALONE) {
            refreshStandAloneVcsTokens(vcs);
        } else {
            refreshOAuthVcsTokens(vcs);
        }
    }

    private void refreshOAuthVcsTokens(Vcs vcs) {
        if (vcs.getStatus().equals(VcsStatus.COMPLETED)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> newTokenInformation = tokenService.refreshAccessToken(
                    vcs.getId().toString(),
                    vcs.getVcsType(),
                    vcs.getTokenExpiration(),
                    vcs.getClientId(),
                    vcs.getClientSecret(),
                    vcs.getRefreshToken(),
                    vcs.getCallback(),
                    vcs.getEndpoint());

            if (!newTokenInformation.isEmpty()) {
                Vcs tempVcs = vcsRepository.getReferenceById(vcs.getId());
                tempVcs.setAccessToken((String) newTokenInformation.get("accessToken"));
                tempVcs.setRefreshToken((String) newTokenInformation.get("refreshToken"));
                tempVcs.setTokenExpiration((Date) newTokenInformation.get("tokenExpiration"));
                vcsRepository.save(tempVcs);
            }
        }

    }

    private void refreshStandAloneVcsTokens(Vcs vcs) {
        log.info("Refreshing Standalone VCS Tokens");
        List<GitHubAppToken> tokens = null;
        try {
            tokens = standAloneTokenService.refreshAccessToken(vcs);
        } catch (JsonProcessingException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.info("Failed to refresh Standalone VCS Tokens, error {}", e);
        }
        if (tokens == null)  return;

        gitHubAppTokenRepository.saveAll(tokens);
    }
}
