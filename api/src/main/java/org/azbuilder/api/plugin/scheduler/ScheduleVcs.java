package org.azbuilder.api.plugin.scheduler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.vcs.TokenService;
import org.azbuilder.api.repository.VcsRepository;
import org.azbuilder.api.rs.vcs.Vcs;
import org.azbuilder.api.rs.vcs.VcsStatus;
import org.azbuilder.api.rs.workspace.schedule.Schedule;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
        Vcs vcs = vcsRepository.getOne(UUID.fromString(vcsId));

        if(vcs.getStatus().equals(VcsStatus.COMPLETED)) {
            Map<String, Object> newTokenInformation = tokenService.refreshAccessToken(
                    vcs.getId().toString(),
                    vcs.getVcsType(),
                    vcs.getTokenExpiration(),
                    vcs.getClientId(),
                    vcs.getClientSecret(),
                    vcs.getRefreshToken());

            if (!newTokenInformation.isEmpty()) {
                Vcs tempVcs = vcsRepository.getOne(vcs.getId());
                tempVcs.setAccessToken((String) newTokenInformation.get("accessToken"));
                tempVcs.setRefreshToken((String) newTokenInformation.get("refreshToken"));
                tempVcs.setTokenExpiration((Date) newTokenInformation.get("tokenExpiration"));
                vcsRepository.save(tempVcs);
            }
        }
    }
}
