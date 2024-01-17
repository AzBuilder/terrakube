package org.terrakube.api.plugin.vcs.provider.gitlab;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.stereotype.Service;
import org.terrakube.api.plugin.vcs.WebhookResult;
import org.terrakube.api.rs.workspace.Workspace;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

@Service
@Slf4j
public class GitLabWebhookService {

    private final ObjectMapper objectMapper;

    @Value("${org.terrakube.hostname}")
    private String hostname;

    public GitLabWebhookService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public WebhookResult processWebhook(String jsonPayload, Map<String, String> headers, String token) {
        WebhookResult result = new WebhookResult();
        result.setBranch("");
        result.setVia("GitLab");
        try {
            // Verify the GitLab token
            String tokenHeader = headers.get("x-gitlab-token");
            if (tokenHeader == null || !tokenHeader.equals(token)) {
                log.error("X-Gitlab-Token header is missing or doesn't match!");
                result.setValid(false);
                return result;
            }

            result.setValid(true);

            log.info("Parsing GitLab webhook payload");

            // Extract event
            JsonNode rootNode = objectMapper.readTree(jsonPayload);
            String event = rootNode.path("object_kind").asText();
            result.setEvent(event);

            if (event.equals("push")) {
                // Extract branch from the ref
                String ref = rootNode.path("ref").asText();
                String extractedBranch = ref.split("/")[2];
                result.setBranch(extractedBranch);

                // Extract the user who triggered the webhook
                JsonNode userNode = rootNode.path("user_username");
                String user = userNode.asText();
                result.setCreatedBy(user);

                result.setFileChanges(new ArrayList());
                try {
                    GitlabWebhookModel gitlabWebhookModel = new ObjectMapper().readValue(jsonPayload, GitlabWebhookModel.class);
                    result.setCommit(gitlabWebhookModel.getCheckoutSha());
                    gitlabWebhookModel.getCommits().forEach(commitData -> {

                        for (String gitlabmodified : commitData.getModified()) {
                            result.getFileChanges().add(gitlabmodified);
                            log.info("Modified Gitlab Object: {}", gitlabmodified);
                        }

                        for (String gitlabRemoved : commitData.getRemoved()) {
                            result.getFileChanges().add(gitlabRemoved);
                            log.info("Removed Gitlab Object: {}", gitlabRemoved);
                        }

                        for (String gitlabAdded : commitData.getAdded()) {
                            log.info("New Gitlab Object: {}", gitlabAdded);
                            result.getFileChanges().add(gitlabAdded);
                        }
                    });
                } catch (JsonProcessingException e) {
                    log.error(e.getMessage());
                }

            }
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON payload", e);
        }
        return result;
    }

    public String createWebhook(Workspace workspace, String webhookId) {
        String id = "";
        String secret = Base64.getEncoder()
                .encodeToString(workspace.getId().toString().getBytes(StandardCharsets.UTF_8));
        String ownerAndRepo = extractOwnerAndRepo(workspace.getSource());
        String webhookUrl = String.format("https://%s/webhook/v1/%s", hostname, webhookId);
        RestTemplate restTemplate = new RestTemplate();

        // Create the headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + workspace.getVcs().getAccessToken());

        // Create the body
        String body = "{\"url\":\"" + webhookUrl
                + "\",\"push_events\":\"true\",\"enable_ssl_verification\":\"false\",\"token\":\"" + secret + "\"}";

        log.info(body);
        // Create the entity
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        URI gitlabUri = UriComponentsBuilder.fromHttpUrl(workspace.getVcs().getApiUrl() + "/projects/" + ownerAndRepo + "/hooks").build(true).toUri();

        // Make the request using the GitLab API
        ResponseEntity<String> response = restTemplate.exchange(
               gitlabUri, HttpMethod.POST, entity, String.class);

        // Extract the id from the response
        if (response.getStatusCodeValue() == 201) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                id = rootNode.path("id").asText();
            } catch (Exception e) {
                log.error("Error parsing JSON response", e);
            }

            log.info("Hook created successfully {}" + id);
        }

        return id;
    }

    private String extractOwnerAndRepo(String repoUrl) {
        try {
            URL url = new URL(repoUrl);
            String[] parts = url.getPath().split("/");
            String owner = parts[1];
            String repo = parts[2].replace(".git", "");
            String ownerAndRepo = owner + "/" + repo;
            return URLEncoder.encode(ownerAndRepo, "UTF-8");
        } catch (Exception e) {
           log.info("Error extracting owner and repo from URL", e);
        }
        return "";
    }
}