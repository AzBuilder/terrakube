package org.terrakube.api.plugin.vcs.provider.github;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.terrakube.api.plugin.vcs.TokenService;
import org.terrakube.api.plugin.vcs.WebhookResult;
import org.terrakube.api.plugin.vcs.WebhookServiceBase;
import org.terrakube.api.rs.job.Job;
import org.terrakube.api.rs.job.JobStatus;
import org.terrakube.api.rs.job.JobVia;
import org.terrakube.api.rs.workspace.Workspace;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GitHubWebhookService extends WebhookServiceBase {

    private final ObjectMapper objectMapper;
    private final TokenService tokenService;

    @Value("${org.terrakube.hostname}")
    private String hostname;
    @Value("${org.terrakube.ui.url}")
    private String uiUrl;

    public GitHubWebhookService(ObjectMapper objectMapper, TokenService tokenService) {
        this.objectMapper = objectMapper;
        this.tokenService = tokenService;
    }

    public WebhookResult processWebhook(String jsonPayload, Map<String, String> headers, String token) {
        return handleWebhook(jsonPayload, headers, token, "x-hub-signature-256", JobVia.Github.name(),
                this::handleEvent);
    }

    private WebhookResult handleEvent(String jsonPayload, WebhookResult result, Map<String, String> headers) {
        // Extract event
        String event = headers.get("x-github-event");
        result.setEvent(event);

        if (event.equals("push")) {
            try {
                // Extract branch from the ref
                JsonNode rootNode = objectMapper.readTree(jsonPayload);
                String[] ref = rootNode.path("ref").asText().split("/");
                String[] extractedBranch = Arrays.copyOfRange(ref, 2, ref.length);
                result.setBranch(String.join("/", extractedBranch));

                // Extract the user who triggered the webhook
                JsonNode pusherNode = rootNode.path("pusher");
                String pusher = pusherNode.path("email").asText();
                result.setCreatedBy(pusher);
            } catch (Exception e) {
                log.error("Error parsing JSON response", e);
                result.setBranch("");
            }

            result.setFileChanges(new ArrayList());
            try {
                GitHubWebhookModel gitHubWebhookModel = new ObjectMapper().readValue(jsonPayload,
                        GitHubWebhookModel.class);
                result.setCommit(gitHubWebhookModel.getHead_commit().getId());
                gitHubWebhookModel.getCommits().forEach(commit -> {
                    for (String addedObject : commit.getAdded()) {
                        log.info("New: {}", addedObject);
                        result.getFileChanges().add(addedObject);
                    }

                    for (String removedObject : commit.getRemoved()) {
                        log.info("Removed: {}", removedObject);
                        result.getFileChanges().add(removedObject);
                    }

                    for (String modifedObject : commit.getModified()) {
                        log.info("Modified: {}", modifedObject);
                        result.getFileChanges().add(modifedObject);
                    }
                });
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
            }
        }

        return result;
    }

    public void sendCommitStatus(Job job, JobStatus jobStatus) {
        Workspace workspace = job.getWorkspace();
        String jobUrl = String.format("%s/organizations/%s/workspaces/%s/runs/%s", uiUrl,
                workspace.getOrganization().getId(), workspace.getId(), job.getId());
        String[] ownerAndRepos = extractOwnerAndRepo(workspace.getSource());

        String token = "";
        try {
            token = tokenService.getAccessToken(ownerAndRepos, workspace.getVcs());
        } catch (JsonProcessingException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Error retrieving tokens for access to owner/organization {}, error {}", ownerAndRepos[0], e);
            return;
        }

        GithubCommitStatus commitStatus = GithubCommitStatus.pending;
        String commitStatusContext = "Terrakube - " + workspace.getOrganization().getName() + " - " + workspace.getName();
        String commitStatusDescription = "Your task is in Terrakube queue.";
        switch (jobStatus) {
            case completed:
                commitStatus = GithubCommitStatus.success;
                commitStatusDescription = "Your task has been completed successfully.";
                break;
            case failed:
            case rejected:
            case cancelled:
                commitStatus = GithubCommitStatus.failure;
                commitStatusDescription = "Your task has failed.";
                break;
            case unknown:
                commitStatus = GithubCommitStatus.error;
                commitStatusDescription = "Your task ran into errors.";
                break;
            default:
                break;
        }

        String apiUrl = workspace.getVcs().getApiUrl() + "/repos/" + String.join("/", ownerAndRepos) + "/statuses/"
                + job.getCommitId();

        // Create the headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.github+json");
        headers.set("Authorization", "Bearer " + token);
        headers.set("X-GitHub-Api-Version", "2022-11-28");

        log.info(String.format("Sending job status %s to GitHub for commit %s", job.getStatus(), job.getCommitId()));
        // Create the body
        String body = "{\"state\":\"" + commitStatus.name()
                + "\",\"description\":\"" + commitStatusDescription + "\",\"target_url\":\""
                + jobUrl + "\",\"context\":\"" + commitStatusContext + "\"}";

        ResponseEntity<String> response = makeApiRequest(headers, body, apiUrl);
        if (response.getStatusCode().value() == 201) {
            log.info("Job status sent successfully to GitHub");
        } else {
            log.error(String.format("Failed to send job status to Github, message %s", response.getBody()));
        }
    }

    public String createWebhook(Workspace workspace, String webhookId) {
        String token = "";
        String[] ownerAndRepo = extractOwnerAndRepo(workspace.getSource());
        try {
            token = tokenService.getAccessToken(ownerAndRepo, workspace.getVcs());
        } catch (JsonProcessingException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Error retrieving tokens for access to owner/organization {}, error {}", ownerAndRepo[0], e);
            return "";
        }

        String url = "";
        String secret = Base64.getEncoder()
                .encodeToString(workspace.getId().toString().getBytes(StandardCharsets.UTF_8));
        String webhookUrl = String.format("https://%s/webhook/v1/%s", hostname, webhookId);

        // Create the headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.github+json");
        headers.set("Authorization", "Bearer " + token);
        headers.set("X-GitHub-Api-Version", "2022-11-28");

        // Create the body, in this version we only support push event but in future we
        // can make this more dynamic
        String body = "{\"name\":\"web\",\"active\":true,\"events\":[\"push\"],\"config\":{\"url\":\"" + webhookUrl
                + "\",\"secret\":\"" + secret + "\",\"content_type\":\"json\",\"insecure_ssl\":\"1\"}}";
        String apiUrl = workspace.getVcs().getApiUrl() + "/repos/" + String.join("/", ownerAndRepo) + "/hooks";

        ResponseEntity<String> response = makeApiRequest(headers, body, apiUrl);
        // Extract the id from the response
        if (response.getStatusCode().value() == 201) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                url = rootNode.path("url").asText();
            } catch (Exception e) {
                log.error("Error parsing JSON response", e);
            }

            log.info("Hook created successfully {}" + url);
        }

        return url;

    }
}
