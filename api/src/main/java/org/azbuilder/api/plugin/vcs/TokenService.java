package org.azbuilder.api.plugin.vcs;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.scheduler.ScheduleVcsService;
import org.azbuilder.api.plugin.vcs.provider.azdevops.AzDevOpsToken;
import org.azbuilder.api.plugin.vcs.provider.azdevops.AzDevOpsTokenService;
import org.azbuilder.api.plugin.vcs.provider.bitbucket.BitBucketToken;
import org.azbuilder.api.plugin.vcs.provider.bitbucket.BitbucketTokenService;
import org.azbuilder.api.plugin.vcs.provider.exception.TokenException;
import org.azbuilder.api.plugin.vcs.provider.github.GitHubToken;
import org.azbuilder.api.plugin.vcs.provider.github.GitHubTokenService;
import org.azbuilder.api.plugin.vcs.provider.gitlab.GitLabToken;
import org.azbuilder.api.plugin.vcs.provider.gitlab.GitLabTokenService;
import org.azbuilder.api.repository.VcsRepository;
import org.azbuilder.api.rs.vcs.Vcs;
import org.azbuilder.api.rs.vcs.VcsStatus;
import org.azbuilder.api.rs.vcs.VcsType;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;

@AllArgsConstructor
@Slf4j
@Service
public class TokenService {

    private static final String QUARTZ_EVERY_60_MINUTES = "0 0 0/1 ? * *";
    private static final String QUARTZ_EVERY_30_MINUTES = "0 0/30 * ? * *";

    VcsRepository vcsRepository;
    GitHubTokenService gitHubTokenService;
    BitbucketTokenService bitbucketTokenService;
    GitLabTokenService gitLabTokenService;
    AzDevOpsTokenService azDevOpsTokenService;
    ScheduleVcsService scheduleVcsService;

    @Transactional
    public boolean generateAccessToken(String vcsId, String tempCode) {
        Vcs vcs = vcsRepository.getOne(UUID.fromString(vcsId));
        int minutes = Calendar.getInstance().get(Calendar.MINUTE);
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
                    //Refresh token every hour, Bitbucket Token expire after 2 hours (7200 seconds)
                    scheduleVcsService.createTask(String.format(QUARTZ_EVERY_60_MINUTES, minutes), vcsId);
                    break;
                case GITLAB:
                    GitLabToken gitLabToken = gitLabTokenService.getAccessToken(vcs.getId().toString(), vcs.getClientId(), vcs.getClientSecret(), tempCode);
                    vcs.setAccessToken(gitLabToken.getAccess_token());
                    vcs.setRefreshToken(gitLabToken.getRefresh_token());
                    vcs.setTokenExpiration(new Date(System.currentTimeMillis() + gitLabToken.getExpires_in() * 1000));
                    //Refresh token every hour, GitLab Token expire after 2 hours (7200 seconds)
                    scheduleVcsService.createTask(String.format(QUARTZ_EVERY_60_MINUTES, minutes), vcsId);
                    break;
                case AZURE_DEVOPS:
                    AzDevOpsToken azDevOpsToken = azDevOpsTokenService.getAccessToken(vcs.getId().toString(), vcs.getClientSecret(), tempCode);
                    vcs.setAccessToken(azDevOpsToken.getAccess_token());
                    vcs.setRefreshToken(azDevOpsToken.getRefresh_token());
                    vcs.setTokenExpiration(new Date(System.currentTimeMillis() + azDevOpsToken.getExpires_in() * 1000));
                    //Refresh token every 30 minutes, Azure DevOps Token expire after 1 hour (3599 seconds)
                    scheduleVcsService.createTask(String.format(QUARTZ_EVERY_30_MINUTES, minutes), vcsId);
                    break;
                default:
                    break;
            }
            vcs.setStatus(VcsStatus.COMPLETED);
            vcsRepository.save(vcs);
        } catch (TokenException e) {
            log.error(e.getMessage());
            vcs.setStatus(VcsStatus.ERROR);
            vcsRepository.save(vcs);
        } catch (SchedulerException e) {
            log.error(e.getMessage());
        } catch (ParseException e) {
            log.error(e.getMessage());
        }

        return true;
    }

    public Map refreshAccessToken(String vcsId, VcsType vcsType, Date tokenExpiration, String clientId, String clientSecret, String refreshToken) {
        Map<String, Object> tokenInformation = new HashMap<>();
        log.info("Renew Token before: {} {}", tokenExpiration, vcsId);
        switch (vcsType) {
            case BITBUCKET:
                try {
                    BitBucketToken bitBucketToken = bitbucketTokenService.refreshAccessToken(clientId, clientSecret, refreshToken);
                    tokenInformation.put("accessToken", bitBucketToken.getAccess_token());
                    tokenInformation.put("refreshToken", bitBucketToken.getRefresh_token());
                    tokenInformation.put("tokenExpiration", new Date(System.currentTimeMillis() + bitBucketToken.getExpires_in() * 1000));
                } catch (TokenException e) {
                    log.error(e.getMessage());
                }
                break;
            case GITLAB:
                try {
                    GitLabToken gitLabToken = gitLabTokenService.refreshAccessToken(vcsId, clientId, clientSecret, refreshToken);
                    tokenInformation.put("accessToken", gitLabToken.getAccess_token());
                    tokenInformation.put("refreshToken", gitLabToken.getRefresh_token());
                    tokenInformation.put("tokenExpiration", new Date(System.currentTimeMillis() + gitLabToken.getExpires_in() * 1000));
                } catch (TokenException e) {
                    log.error(e.getMessage());
                }
                break;
            case AZURE_DEVOPS:
                try {
                    AzDevOpsToken azDevOpsToken = azDevOpsTokenService.refreshAccessToken(vcsId, clientSecret, refreshToken);
                    tokenInformation.put("accessToken", azDevOpsToken.getAccess_token());
                    tokenInformation.put("refreshToken", azDevOpsToken.getRefresh_token());
                    tokenInformation.put("tokenExpiration", new Date(System.currentTimeMillis() + azDevOpsToken.getExpires_in() * 1000));
                } catch (TokenException e) {
                    log.error(e.getMessage());
                }
                break;
            default:
                break;
        }
        return tokenInformation;
    }

}
