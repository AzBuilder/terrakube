package org.terrakube.api.plugin.vcs.provider.gitlab;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.terrakube.api.plugin.vcs.WebhookResult;
import org.terrakube.api.plugin.vcs.WebhookServiceBase;
import org.terrakube.api.rs.workspace.Workspace;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GitLabWebhookService extends WebhookServiceBase {

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
                String[] ref = rootNode.path("ref").asText().split("/");
                String[] extractedBranch = Arrays.copyOfRange(ref, 2, ref.length);
                result.setBranch(String.join("/", extractedBranch));

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
        String ownerAndRepo = String.join("/", extractOwnerAndRepo(workspace.getSource()));
        String token = workspace.getVcs().getAccessToken();
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
        String projectId = "";
        try {
            log.info("Search gitlab project id using {}, {}", ownerAndRepo, workspace.getVcs().getApiUrl());
            projectId = getGitlabProjectId(ownerAndRepo, token, workspace.getVcs().getApiUrl());
        } catch (InterruptedException | IOException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
        URI gitlabUri = UriComponentsBuilder.fromHttpUrl(workspace.getVcs().getApiUrl() + "/projects/" + projectId + "/hooks").build(true).toUri();

        // Make the request using the GitLab API
        ResponseEntity<String> response = restTemplate.exchange(
               gitlabUri, HttpMethod.POST, entity, String.class);

        // Extract the id from the response
        if (response.getStatusCode().value() == 201) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                id = rootNode.path("id").asText();
            } catch (Exception e) {
                log.error("Error parsing JSON response", e);
            }

            log.info("GitHub Hook created successfully for workspace {}/{} with id {}",
                    workspace.getOrganization().getName(), workspace.getName(), id);
        }

        return id;
    }

    private String getGitlabProjectId(String ownerAndRepo, String accessToken, String gitlabBaseUrl) throws IOException, InterruptedException {
        String projectId = "";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(gitlabBaseUrl + "/search?scope=projects&search=" + ownerAndRepo))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check for successful response
        if (response.statusCode() == 200) {
            log.info("Response from Gitlab: {}" , response.body());
            // Initialize Jackson ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();

            // Parse the JSON string into a JsonNode
            JsonNode jsonNode = objectMapper.readTree(response.body());

            projectId = jsonNode.get(0).get("id").asText();
            log.info("Parsed Project ID: {}", projectId);
        } else {
            log.error("Failed to retrieve project ID. HTTP Status: {}", response.statusCode());
            log.error("Response: {}", response.body());
        }
        return projectId;
    }
    
    public void deleteWebhook(Workspace workspace, String webhookRemoteId) {
        String ownerAndRepo = String.join("/", extractOwnerAndRepo(workspace.getSource()));
        String apiUrl = workspace.getVcs().getApiUrl() + "/projects/" + ownerAndRepo + "/hooks/" + webhookRemoteId;
        
        ResponseEntity<String> response = callGitlabApi(workspace.getVcs().getAccessToken(), "", apiUrl, HttpMethod.DELETE);
        if (response.getStatusCode().value() == 204) {
            log.info("Webhook with remote hook id {} on repository {} deleted successfully", webhookRemoteId, ownerAndRepo);
        } else {
            log.warn("Failed to delete webhook with remote hook id {} on repository {}, message {}", webhookRemoteId, ownerAndRepo, response.getBody());
        }
    }

    private ResponseEntity<String> callGitlabApi(String token, String body, String apiUrl, HttpMethod httpMethod) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        
        ResponseEntity<String> response = makeApiRequest(headers, body, apiUrl, httpMethod);
        
        return response;
    }
}