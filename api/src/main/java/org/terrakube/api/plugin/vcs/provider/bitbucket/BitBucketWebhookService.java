package org.terrakube.api.plugin.vcs.provider.bitbucket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.terrakube.api.plugin.vcs.WebhookResult;
import org.terrakube.api.plugin.vcs.WebhookServiceBase;
import org.terrakube.api.repository.WorkspaceRepository;
import org.terrakube.api.rs.workspace.Workspace;

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

    @Value("${org.terrakube.hostname}")
    private String hostname;

    private WorkspaceRepository workspaceRepository;

    public BitBucketWebhookService(WorkspaceRepository workspaceRepository, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.workspaceRepository =workspaceRepository;
    }

    public WebhookResult processWebhook(String jsonPayload, Map<String, String> headers, String token) {
        return handleWebhook(jsonPayload, headers, token, "x-hub-signature", "Bitbucket", this::handleEvent);
    }

    private WebhookResult handleEvent(String jsonPayload, WebhookResult result, Map<String, String> headers){
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

                BitbucketTokenModel bitbucketTokenModel = new ObjectMapper().readValue(jsonPayload, BitbucketTokenModel.class);
                if(bitbucketTokenModel.getPush().getChanges().size() == 1) {
                    log.info("Bitbucket commit: {}", bitbucketTokenModel.getPush().getChanges().get(0).getNewCommit().getTarget().getHash());
                    log.info("Bitbucket diff file: {}", bitbucketTokenModel.getPush().getChanges().get(0).getLinks().getDiff().getHref());
                    result.setCommit(bitbucketTokenModel.getPush().getChanges().get(0).getNewCommit().getTarget().getHash());
                    result.setFileChanges(getFileChanges(bitbucketTokenModel.getPush().getChanges().get(0).getLinks().getDiff().getHref(), result.getWorkspaceId()));
                } else {
                    log.error("Bitbucket webhook with more than 1 changes is not supported");
                }
            } catch (Exception e) {
                log.error("Error parsing JSON response", e);
                result.setBranch("");
            }
        }

        return result;
    }

    private List<String> getFileChanges(String diffFile, String workspaceId) {
        List<String> fileChanges = new ArrayList<>();

        try {
            String accessToken = "Bearer " + workspaceRepository.findById(UUID.fromString(workspaceId)).get().getVcs().getAccessToken();
            URL urlBitbucketApi = new URL(diffFile);
            log.info("Base URL: {}", String.format("%s://%s", urlBitbucketApi.getProtocol(), urlBitbucketApi.getHost()));
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

            new BufferedReader(new StringReader(diffContent)).lines().forEach(line ->{
                if(line.startsWith("diff --git ")){
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
        String url = "";
        String secret = Base64.getEncoder()
                .encodeToString(workspace.getId().toString().getBytes(StandardCharsets.UTF_8));
        String[] ownerAndRepo = extractOwnerAndRepo(workspace.getSource());
        String webhookUrl = String.format("https://%s/webhook/v1/%s", hostname, webhookId);

        // Create the headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Authorization", "Bearer " + workspace.getVcs().getAccessToken());

        // Create the body, in this version we only support push event but in future we
        // can make this more dynamic
        String body = "{\"description\":\"Terrakube\",\"url\":\"" + webhookUrl
                + "\",\"active\":true,\"events\":[\"repo:push\"],\"secret\":\"" + secret + "\"}";

        String apiUrl = workspace.getVcs().getApiUrl() + "/repositories/" + String.join("/", ownerAndRepo) + "/hooks";

        ResponseEntity<String> response = makeApiRequest(headers, body, apiUrl);

        // Extract the id from the response
        if (response.getStatusCode().value() == 201) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                url = rootNode.path("links").path("self").path("href").asText();
            } catch (Exception e) {
                log.error("Error parsing JSON response", e);
            }

            log.info("Hook created successfully {}" + url);
        } else {
            log.error("Error creating the webhook" + response.getBody());
        }

        return url;
    }
}
