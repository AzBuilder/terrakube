package io.terrakube.api.plugin.vcs.provider.bitbucket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import io.terrakube.api.plugin.vcs.WebhookResult;
import io.terrakube.api.plugin.vcs.WebhookServiceBase;
import io.terrakube.api.repository.WorkspaceRepository;
import io.terrakube.api.rs.workspace.Workspace;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

@Service
@Slf4j
public class BitBucketWebhookService extends WebhookServiceBase {

    private final ObjectMapper objectMapper;

    @Value("${io.terrakube.hostname}")
    private String hostname;

    private WorkspaceRepository workspaceRepository;

    public BitBucketWebhookService(WorkspaceRepository workspaceRepository, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.workspaceRepository = workspaceRepository;
    }

    public WebhookResult processWebhook(String jsonPayload, Map<String, String> headers, String token) {
        return handleWebhook(jsonPayload, headers, token, "x-hub-signature", "Bitbucket", this::handleEvent);
    }

    private WebhookResult handleEvent(String jsonPayload, WebhookResult result, Map<String, String> headers) {
        // Extract event
        String event = headers.get("x-event-key");
        if (event != null) {
            String[] parts = event.split(":");
            if (parts.length > 1) {
                result.setEvent(parts[1]); // Set the second part of the split string
            } else {
                result.setEvent(event); // If there's no ":", set the whole event
            }
        }

        if (result.getEvent().equals("push")) {
            handlePushEvent(jsonPayload, result);
        } else if (result.getEvent().equals("pullrequest")) {
            handlePullRequestEvent(jsonPayload, result);
        } else if (result.getEvent().equals("release")) {
            handleReleaseEvent(jsonPayload, result);
        }

        return result;
    }

    private void handlePushEvent(String jsonPayload, WebhookResult result) {
        result.setEvent("push");
        try {
            // Extract branch from the changes
            JsonNode rootNode = objectMapper.readTree(jsonPayload);
            JsonNode changesNode = rootNode.path("push").path("changes").get(0);
            String ref = changesNode.path("new").path("name").asText();
            result.setBranch(ref);

            // Extract the user who triggered the webhook
            JsonNode authorNode = changesNode.path("new").path("target").path("author").path("raw");
            String author = authorNode.asText();
            result.setCreatedBy(author);

            BitbucketTokenModel bitbucketTokenModel = new ObjectMapper().readValue(jsonPayload,
                    BitbucketTokenModel.class);
            if (bitbucketTokenModel.getPush().getChanges().size() == 1) {
                log.info("Bitbucket commit: {}",
                        bitbucketTokenModel.getPush().getChanges().get(0).getNewCommit().getTarget().getHash());
                log.info("Bitbucket diff file: {}",
                        bitbucketTokenModel.getPush().getChanges().get(0).getLinks().getDiff().getHref());
                result.setCommit(
                        bitbucketTokenModel.getPush().getChanges().get(0).getNewCommit().getTarget().getHash());
                result.setFileChanges(getFileChanges(
                        bitbucketTokenModel.getPush().getChanges().get(0).getLinks().getDiff().getHref(),
                        result.getWorkspaceId()));
            } else {
                log.error("Bitbucket webhook with more than 1 changes is not supported");
            }
        } catch (Exception e) {
            log.error("Error parsing push event JSON response", e);
            result.setBranch("");
        }
    }

    private void handlePullRequestEvent(String jsonPayload, WebhookResult result) {
        result.setEvent("pull_request");
        try {
            JsonNode rootNode = objectMapper.readTree(jsonPayload);
            JsonNode pullRequestNode = rootNode.path("pullrequest");
            
            // Extract source branch
            String sourceBranch = pullRequestNode.path("source").path("branch").path("name").asText();
            result.setBranch(sourceBranch);
            
            // Extract the user who created the pull request
            String author = pullRequestNode.path("author").path("display_name").asText();
            result.setCreatedBy(author);
            
            // Extract commit information
            String commit = pullRequestNode.path("source").path("commit").path("hash").asText();
            result.setCommit(commit);
            
            // For pull requests, we can try to get the diff URL if available
            JsonNode linksNode = pullRequestNode.path("links");
            if (linksNode.has("diff")) {
                String diffUrl = linksNode.path("diff").path("href").asText();
                result.setFileChanges(getFileChanges(diffUrl, result.getWorkspaceId()));
            }
            
            log.info("Bitbucket pull request event processed for branch: {}", sourceBranch);
        } catch (Exception e) {
            log.error("Error parsing pull request event JSON response", e);
            result.setBranch("");
        }
    }

    private void handleReleaseEvent(String jsonPayload, WebhookResult result) {
        result.setEvent("release");
        try {
            JsonNode rootNode = objectMapper.readTree(jsonPayload);
            JsonNode releaseNode = rootNode.path("release");
            
            // Extract tag name
            String tagName = releaseNode.path("tag_name").asText();
            result.setBranch(tagName);
            result.setRelease(true);
            
            // Extract the user who created the release
            String author = releaseNode.path("author").path("display_name").asText();
            result.setCreatedBy(author);
            
            // Extract commit information if available
            String commit = releaseNode.path("target").path("hash").asText();
            result.setCommit(commit);
            
            log.info("Bitbucket release event processed for tag: {}", tagName);
        } catch (Exception e) {
            log.error("Error parsing release event JSON response", e);
            result.setBranch("");
        }
    }

    private List<String> getFileChanges(String diffFile, String workspaceId) {
        List<String> fileChanges = new ArrayList<>();

        try {
            String accessToken = "Bearer "
                    + workspaceRepository.findById(UUID.fromString(workspaceId)).get().getVcs().getAccessToken();
            URL urlBitbucketApi = new URL(diffFile);
            log.info("Base URL: {}",
                    String.format("%s://%s", urlBitbucketApi.getProtocol(), urlBitbucketApi.getHost()));
            log.info("URI: {}", urlBitbucketApi.getPath());
            WebClient webClient = WebClient.builder()
                    .baseUrl(String.format("%s://%s", urlBitbucketApi.getProtocol(), urlBitbucketApi.getHost()))
                    .defaultHeader(HttpHeaders.AUTHORIZATION, accessToken)
                    .build();

            String diffContent = webClient.get()
                    .uri(urlBitbucketApi.getPath())
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            new BufferedReader(new StringReader(diffContent)).lines().forEach(line -> {
                if (line.startsWith("diff --git ")) {
                    log.warn("Checking change: {}", line);
                    // Example
                    // diff --git a/work1/main.tf b/work1/main.tf
                    String file = line.split("\\s+")[2].substring(2);
                    log.warn("Adding file: {}", file);
                    fileChanges.add(file);
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return fileChanges;
    }

    public String createWebhook(Workspace workspace, String webhookId) {
        String id = "";
        String secret = Base64.getEncoder()
                .encodeToString(workspace.getId().toString().getBytes(StandardCharsets.UTF_8));
        String[] ownerAndRepo = extractOwnerAndRepo(workspace.getSource());
        String webhookUrl = String.format("https://%s/webhook/v1/%s", hostname, webhookId);

        // Create the body with support for push, pull request, and release events
        String body = "{\"description\":\"Terrakube\",\"url\":\"" + webhookUrl
                + "\",\"active\":true,\"events\":[\"repo:push\",\"pullrequest:created\",\"pullrequest:updated\",\"repo:release\"],\"secret\":\"" + secret + "\"}";

        String apiUrl = workspace.getVcs().getApiUrl() + "/repositories/" + String.join("/", ownerAndRepo) + "/hooks";

        ResponseEntity<String> response = callBitBucketApi(workspace.getVcs().getAccessToken(), body, apiUrl,
                HttpMethod.POST);

        // Extract the id from the response
        if (response.getStatusCode().value() == 201) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                id = rootNode.path("uuid").asText();
            } catch (Exception e) {
                log.error("Error parsing JSON response", e);
            }

            log.info("Bitbucket Hook created successfully for workspace {}/{} with id {}",
                    workspace.getOrganization().getName(), workspace.getName(), id);
        } else {
            log.error("Error creating the webhook" + response.getBody());
        }

        return id;
    }

    public void deleteWebhook(Workspace workspace, String webhookRemoteId) {
        String apiUrl = webhookRemoteId;
        String ownerAndRepo = String.join("/", extractOwnerAndRepo(workspace.getSource()));

        if (!webhookRemoteId.substring(0, 4).equals("http")) {
            apiUrl = workspace.getVcs().getApiUrl() + "/repositories/" + ownerAndRepo + "/hooks/" + webhookRemoteId;
        }

        ResponseEntity<String> response = callBitBucketApi(workspace.getVcs().getAccessToken(), "", apiUrl,
                HttpMethod.DELETE);
        if (response.getStatusCode().value() == 204) {
            log.info("Webhook with remote hook id {} on repository {} deleted successfully", webhookRemoteId,
                    ownerAndRepo);
        } else {
            log.warn("Failed to delete webhook with remote hook id {} on repository {}, message {}", webhookRemoteId,
                    ownerAndRepo, response.getBody());
        }
    }

    private ResponseEntity<String> callBitBucketApi(String token, String body, String apiUrl, HttpMethod httpMethod) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Authorization", "Bearer " + token);

        return makeApiRequest(headers, body, apiUrl, httpMethod);
    }
}