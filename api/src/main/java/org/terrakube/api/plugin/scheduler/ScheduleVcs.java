package org.terrakube.api.plugin.scheduler;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.terrakube.api.plugin.vcs.TokenService;
import org.terrakube.api.repository.GitHubAppTokenRepository;
import org.terrakube.api.repository.VcsRepository;
import org.terrakube.api.rs.vcs.Vcs;
import org.terrakube.api.rs.vcs.VcsStatus;

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
}
