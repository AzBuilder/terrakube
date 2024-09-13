package org.terrakube.api.plugin.scheduler.inactive;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terrakube.api.plugin.vcs.provider.github.GitHubWebhookService;
import org.terrakube.api.repository.JobRepository;
import org.terrakube.api.repository.StepRepository;
import org.terrakube.api.rs.job.Job;
import org.terrakube.api.rs.job.JobStatus;
import org.terrakube.api.rs.job.JobVia;
import org.terrakube.api.rs.job.step.Step;

import java.util.Arrays;
import java.util.Date;

import static org.terrakube.api.plugin.scheduler.ScheduleJobService.PREFIX_JOB_CONTEXT;
import static org.terrakube.api.rs.vcs.VcsType.GITHUB;

@Service
@Slf4j
@AllArgsConstructor
public class InactiveJobs implements org.quartz.Job {

    JobRepository jobRepository;
    RedisTemplate redisTemplate;
    GitHubWebhookService gitHubWebhookService;
    StepRepository stepRepository;

    @Transactional
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        for (Job job : jobRepository.findAllByStatusInOrderByIdAsc(Arrays.asList(
                JobStatus.pending,
                JobStatus.running,
                JobStatus.queue,
                JobStatus.waitingApproval))) {
            Date currentTime = new Date(System.currentTimeMillis());
            Date jobExpirationDate = DateUtils.addHours(job.getCreatedDate(), 6);
            log.info("Inactive Job {} should be completed before {}, current time {}", job.getId(), jobExpirationDate, currentTime);
            if (currentTime.after(jobExpirationDate)) {
                try {
                    log.warn("Deleting Job Context {} from Quartz", PREFIX_JOB_CONTEXT + job.getId());
                    log.error("Job has been running for more than 6 hours, cancelling running job");
                    job.setStatus(JobStatus.failed);
                    jobRepository.save(job);
                    redisTemplate.delete(String.valueOf(job.getId()));
                    log.warn("Cancelling pending steps");
                    for (Step step : stepRepository.findByJobId(job.getId())) {
                        if (step.getStatus().equals(JobStatus.pending) || step.getStatus().equals(JobStatus.running)) {
                            step.setStatus(JobStatus.failed);
                            stepRepository.save(step);
                        }
                    }
                    if (job.getVia().equals(JobVia.CLI.name()) || job.getVia().equals(JobVia.UI.name()) ) {
                        log.info("No information to update for job", job.getId());
                        return;
                    } else {

                        switch (job.getWorkspace().getVcs().getVcsType()) {
                            case GITHUB:
                                log.info("Updating VCS information for GITHUB", job.getId());
                                gitHubWebhookService.sendCommitStatus(job, JobStatus.unknown);
                                break;
                            default:
                                break;
                        }
                    }
                    jobExecutionContext.getScheduler().deleteJob(new JobKey(PREFIX_JOB_CONTEXT + job.getId()));
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
                log.warn("Closing Job");
                return;
            }
        }
    }

}
