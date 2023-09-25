package org.terrakube.api.plugin.scheduler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.terrakube.api.plugin.vcs.TokenService;
import org.terrakube.api.repository.VcsRepository;
import org.terrakube.api.rs.vcs.Vcs;
import org.terrakube.api.rs.vcs.VcsStatus;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Component
@Getter
@Setter
@Slf4j
public class ScheduleVcs implements org.quartz.Job {

    public static final String VCS_ID = "vcsId";

    TokenService tokenService;
    VcsRepository vcsRepository;

    @Transactional
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String vcsId = jobExecutionContext.getJobDetail().getJobDataMap().getString(VCS_ID);
        boolean vcsDeleted = false;
        Vcs vcs = null;

        try {
            vcs = vcsRepository.getReferenceById(UUID.fromString(vcsId));
        } catch (EntityNotFoundException ex) {
            log.warn("VCS {} was deleted, job is still active but is no longer need it", vcsId);
            vcsDeleted = true;
        }

        if (!vcsDeleted && vcs != null && vcs.getStatus().equals(VcsStatus.COMPLETED)) {
            Map<String, Object> newTokenInformation = tokenService.refreshAccessToken(
                    vcs.getId().toString(),
                    vcs.getVcsType(),
                    vcs.getTokenExpiration(),
                    vcs.getClientId(),
                    vcs.getClientSecret(),
                    vcs.getRefreshToken(),
                    vcs.getCallback());

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
