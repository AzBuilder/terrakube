package org.terrakube.api.plugin.vcs.provider.github;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.terrakube.api.plugin.vcs.WebhookResult;
import org.terrakube.api.plugin.vcs.WebhookServiceBase;
import org.terrakube.api.rs.workspace.Workspace;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

@Service
@Slf4j
public class GitHubWebhookService extends WebhookServiceBase {

    private final ObjectMapper objectMapper;

    @Value("${org.terrakube.hostname}")
    private String hostname;

    public GitHubWebhookService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public WebhookResult processWebhook(String jsonPayload, Map<String, String> headers, String token) {
        return handleWebhook(jsonPayload, headers, token, "x-hub-signature-256", "Github", this::handleEvent);
    }

    private WebhookResult handleEvent(String jsonPayload, WebhookResult result,  Map<String, String> headers) {
        // Extract event
        String event = headers.get("x-github-event");
        result.setEvent(event);

        if (event.equals("push")) {
            try{
            // Extract branch from the ref
            JsonNode rootNode = objectMapper.readTree(jsonPayload);
            String ref = rootNode.path("ref").asText();
            String extractedBranch = ref.split("/")[2];
            result.setBranch(extractedBranch);


            // Extract the user who triggered the webhook
            JsonNode pusherNode = rootNode.path("pusher");
            String pusher = pusherNode.path("email").asText();
            result.setCreatedBy(pusher);
            }
            catch(Exception e)
            {
                log.error("Error parsing JSON response", e);
                result.setBranch("");
            }

            result.setFileChanges(new ArrayList());
            try {
                GitHubWebhookModel gitHubWebhookModel = new ObjectMapper().readValue(jsonPayload, GitHubWebhookModel.class);
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

    public String createWebhook(Workspace workspace, String webhookId) {
        String url = "";
        String secret = Base64.getEncoder()
                .encodeToString(workspace.getId().toString().getBytes(StandardCharsets.UTF_8));
        String ownerAndRepo = extractOwnerAndRepo(workspace.getSource());
        String webhookUrl = String.format("https://%s/webhook/v1/%s", hostname, webhookId);

        // Create the headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.github+json");
        headers.set("Authorization", "Bearer " + workspace.getVcs().getAccessToken());
        headers.set("X-GitHub-Api-Version", "2022-11-28");

        // Create the body, in this version we only support push event but in future we
        // can make this more dynamic
        String body = "{\"name\":\"web\",\"active\":true,\"events\":[\"push\"],\"config\":{\"url\":\"" + webhookUrl
                + "\",\"secret\":\"" + secret + "\",\"content_type\":\"json\",\"insecure_ssl\":\"1\"}}";
        String apiUrl = workspace.getVcs().getApiUrl() + "/repos/" + ownerAndRepo + "/hooks";

        ResponseEntity<String> response = makeApiRequest(headers, body, apiUrl);
        // Extract the id from the response
        if (response.getStatusCodeValue() == 201) {
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
