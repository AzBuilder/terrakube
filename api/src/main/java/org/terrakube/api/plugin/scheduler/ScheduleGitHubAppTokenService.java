package org.terrakube.api.plugin.scheduler;

import org.quartz.Job;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Getter
public class ScheduleGitHubAppTokenService extends ScheduleServiceBase {

    @Autowired
    Scheduler scheduler;

    private final String jobPrefix = "TerrakubeV2_GitHubAppToken_";
    private final Class<? extends Job> jobClass = ScheduleGitHubAppToken.class;
    private final String jobType = "GitHubAppToken";
    private final String jobDataKey = "githubAppTokenId";
}
