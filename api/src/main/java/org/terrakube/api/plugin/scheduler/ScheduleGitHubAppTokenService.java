package org.terrakube.api.plugin.scheduler;

import org.quartz.Job;
import org.springframework.stereotype.Service;

import lombok.Getter;

@Service
@Getter
public class ScheduleGitHubAppTokenService extends ScheduleServiceBase {
    private final String jobPrefix = "TerrakubeV2_GitHubAppToken_";
    private final Class<? extends Job> jobClass = ScheduleGitHubAppToken.class;
    private final String jobType = "GitHubAppToken";
    private final String jobDataKey = "githubAppTokenId";
}
