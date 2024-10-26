package org.terrakube.api.plugin.vcs;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terrakube.api.plugin.scheduler.ScheduleJobService;
import org.terrakube.api.plugin.vcs.provider.bitbucket.BitBucketWebhookService;
import org.terrakube.api.plugin.vcs.provider.github.GitHubWebhookService;
import org.terrakube.api.plugin.vcs.provider.gitlab.GitLabWebhookService;
import org.terrakube.api.repository.JobRepository;
import org.terrakube.api.repository.WebhookRepository;
import org.terrakube.api.rs.job.Job;
import org.terrakube.api.rs.job.JobStatus;
import org.terrakube.api.rs.vcs.Vcs;
import org.terrakube.api.rs.webhook.Webhook;
import org.terrakube.api.rs.webhook.WebhookEvent;
import org.terrakube.api.rs.workspace.Workspace;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Service
public class WebhookService {

    WebhookRepository webhookRepository;
    GitHubWebhookService gitHubWebhookService;
    GitLabWebhookService gitLabWebhookService;
    BitBucketWebhookService bitBucketWebhookService;
    JobRepository jobRepository;
    ScheduleJobService scheduleJobService;
    ObjectMapper objectMapper;

    @Transactional
    public String processWebhook(String webhookId, String jsonPayload, Map<String, String> headers) {
        String result = "";
        Webhook webhook = webhookRepository.getReferenceById(UUID.fromString(webhookId));
        if (webhook == null) {
            log.error("Webhook {} not found", webhookId);
            return result;
        }
        Workspace workspace = webhook.getWorkspace();
        Vcs vcs = workspace.getVcs();

        // if the VCS is empty we cannot process the webhook
        if (vcs == null) {
            log.error("VCS not found for workspace {} with id {}", workspace.getName(), workspace.getId());
            return result;
        }

        WebhookResult webhookResult = new WebhookResult();
        String base64WorkspaceId = Base64.getEncoder()
                .encodeToString(workspace.getId().toString().getBytes(StandardCharsets.UTF_8));
        switch (vcs.getVcsType()) {
            case GITHUB:
                webhookResult = gitHubWebhookService.processWebhook(jsonPayload, headers,
                        base64WorkspaceId);
                break;
            case GITLAB:
                webhookResult = gitLabWebhookService.processWebhook(jsonPayload, headers,
                        base64WorkspaceId);
                break;
            case BITBUCKET:
                webhookResult = bitBucketWebhookService.processWebhook(jsonPayload, headers,
                        base64WorkspaceId);
                break;
            default:
                break;
        }

        log.info("webhook result {}", webhookResult);

        if (!webhookResult.isValid())
            return result;

        String webhookBranch = webhookResult.getBranch();

        // Return if branch in the event doesn't match any set branches or if the file
        // changes doesn't match the set path
        if (!checkBranch(webhookBranch, webhook) || !checkFileChanges(webhookResult.getFileChanges(), webhook)) {
            return result;
        }

        try {
            String templateId = webhook.getTemplateId();

            // If the webhook branch is the same as the default workspace branch, or the
            // webhook template is not valid, use the default template of the workspace.
            if (webhookResult.getBranch().equals(workspace.getBranch()) || templateId == null || templateId.isEmpty()) {
                templateId = workspace.getDefaultTemplate();
            }
            // If the template is still not valid, log an error and return
            if (templateId == null || templateId.isEmpty()) {
                log.error(
                        "No valid template found for the configured webhook event {}, nor default template configured for workspace {}",
                        webhook.getEvent(), workspace.getName());
                return result;

            }
            log.info("webhook event {} for workspace {}, using template with id {}", webhookResult.getEvent(),
                    webhook.getWorkspace().getName(), templateId);
            Job job = new Job();
            job.setTemplateReference(templateId);
            job.setRefresh(true);
            job.setPlanChanges(true);
            job.setRefreshOnly(false);
            job.setOverrideBranch(webhookBranch);
            job.setOrganization(workspace.getOrganization());
            job.setWorkspace(workspace);
            job.setCreatedBy(webhookResult.getCreatedBy());
            job.setUpdatedBy(webhookResult.getCreatedBy());
            Date triggerDate = new Date(System.currentTimeMillis());
            job.setCreatedDate(triggerDate);
            job.setUpdatedDate(triggerDate);
            job.setVia(webhookResult.getVia());
            job.setCommitId(webhookResult.getCommit());
            Job savedJob = jobRepository.save(job);
            sendCommitStatus(savedJob);
            scheduleJobService.createJobContext(savedJob);
        } catch (Exception e) {
            log.error("Error creating the job", e);
        }
        return result;
    }

    @Transactional
    public void createWorkspaceWebhook(Webhook webhook) {
        Workspace workspace = webhook.getWorkspace();
        if (workspace.getVcs() == null) {
            log.warn("There is no VCS defined for workspace {}, skipping webhook creation", workspace.getName());
            throw new IllegalArgumentException("No VCS defined for workspace");
        }

        if (webhook.getEvent() == null || webhook.getEvent().toString().isEmpty())
            webhook.setEvent(WebhookEvent.PUSH);

        String webhookRemoteId = "";
        if (webhook.getTemplateId() == null)
            webhook.setTemplateId(workspace.getDefaultTemplate());

        Vcs vcs = workspace.getVcs();
        switch (vcs.getVcsType()) {
            case GITHUB:
                webhookRemoteId = gitHubWebhookService.createWebhook(workspace, webhook.getId().toString());
                break;
            case GITLAB:
                webhookRemoteId = gitLabWebhookService.createWebhook(workspace, webhook.getId().toString());
                break;
            case BITBUCKET:
                webhookRemoteId = bitBucketWebhookService.createWebhook(workspace, webhook.getId().toString());
                break;
            default:
                break;
        }

        if (webhookRemoteId.isEmpty()) {
            log.error("Error creating the webhook");
            throw new IllegalArgumentException("Error creating the webhook");
        }

        webhook.setRemoteHookId(webhookRemoteId);
    }

    @Transactional
    public void deleteWorkspaceWebhook(Webhook webhook) {
        Workspace workspace = webhook.getWorkspace();
        if (workspace.getVcs() == null) {
            log.warn("There is no VCS defined for workspace {}, skipping webhook creation", workspace.getName());
            return;
        }
        if (webhook.getRemoteHookId() == null || webhook.getRemoteHookId().isEmpty()) {
            log.warn("No remote hook id found for webhook {} on workspace {}, skipping webhook deletion",
                    webhook.getId(), workspace.getName());
            return;
        }

        Vcs vcs = workspace.getVcs();
        switch (vcs.getVcsType()) {
            case GITHUB:
                gitHubWebhookService.deleteWebhook(workspace, webhook.getRemoteHookId());
                break;
            case GITLAB:
                gitLabWebhookService.deleteWebhook(workspace, webhook.getRemoteHookId());
                break;
            case BITBUCKET:
                bitBucketWebhookService.deleteWebhook(workspace, webhook.getRemoteHookId());
                break;
            default:
                break;
        }
    }

    private boolean checkBranch(String webhookBranch, Webhook webhook) {
        String[] branchList = webhook.getBranch().split(",");

        for (String branch : branchList) {
            branch = branch.trim();
            if (webhookBranch.matches(branch)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkFileChanges(List<String> files, Webhook webhook) {
        String[] triggeredPath = webhook.getPath().split(",");
        String workspaceFolder = webhook.getWorkspace().getFolder();
        if (workspaceFolder.substring(0, 1).equals("/")) {
            workspaceFolder = workspaceFolder.substring(1);
        }
        for (String file : files) {
            for (int i = 0; i < triggeredPath.length; i++) {
                if (file.matches(triggeredPath[i])) {
                    log.info("Changed file {} matches set trigger pattern {}", file, triggeredPath[i]);
                    return true;
                }
            }
        }
        log.info("Changed files {} doesn't match any of the trigger path pattern {}", files, triggeredPath);
        return false;
    }

    private void sendCommitStatus(Job job) {
        switch (job.getWorkspace().getVcs().getVcsType()) {
            case GITHUB:
                gitHubWebhookService.sendCommitStatus(job, JobStatus.pending);
                break;
            default:
                break;
        }
    }
}
