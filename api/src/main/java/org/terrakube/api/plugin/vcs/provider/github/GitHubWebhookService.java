package org.terrakube.api.plugin.vcs.provider.github;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;
import org.terrakube.api.plugin.vcs.WebhookResult;
import org.terrakube.api.rs.workspace.Workspace;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;

import com.fasterxml.jackson.core.JsonProcessingException;

@Service
@Slf4j
public class GitHubWebhookService {

    private final ObjectMapper objectMapper;

    @Value("${org.terrakube.hostname}")
    private String hostname;

    public GitHubWebhookService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public WebhookResult processWebhook(String jsonPayload,Map<String, String> headers, String token) {
        WebhookResult result = new WebhookResult();
        result.setBranch("");
        result.setVia("Github");
        try {

            log.info("verify signature for github webhook");
            result.setValid(true);
            // Verify the Github signature
            String signatureHeader = headers.get("x-hub-signature-256");
            if (signatureHeader == null) {
                log.error("X-Hub-Signature-256 header is missing!");
                result.setValid(false);
                return result;
            }
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(token.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] computedHash = mac.doFinal(jsonPayload.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = "sha256=" + bytesToHex(computedHash);

            if (!signatureHeader.equals(expectedSignature)) {
               log.error("Request signature didn't match!");
               result.setValid(false);
               return result;
            }

            log.info("Parsing github webhook payload");

            // Extract event
            String event = headers.get("x-github-event");
            result.setEvent(event);

            if(event.equals("push"))
            {
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




        } catch (JsonProcessingException e) {
            log.info("Error processing the webhook", e);
        } catch (NoSuchAlgorithmException e) {
            log.info("Error processing the webhook", e);
        } catch (InvalidKeyException e) {
            log.info("Error parsing the secret", e);
        }
        return result;
    }

public String createWebhook(Workspace workspace,String webhookId)
{
    String url = "";
    String secret = Base64.getEncoder().encodeToString(workspace.getId().toString().getBytes(StandardCharsets.UTF_8));
    String ownerAndRepo = extractOwnerAndRepo(workspace.getSource());
    String webhookUrl = String.format("https://%s/webhook/v1/%s", hostname, webhookId);
    RestTemplate restTemplate = new RestTemplate();

    // Create the headers
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", "application/vnd.github+json");
    headers.set("Authorization", "Bearer " + workspace.getVcs().getAccessToken());
    headers.set("X-GitHub-Api-Version", "2022-11-28");

    // Create the body, in this version we only support push event but in future we can make this more dynamic
    String body = "{\"name\":\"web\",\"active\":true,\"events\":[\"push\"],\"config\":{\"url\":\""+webhookUrl+"\",\"secret\":\""+ secret +"\",\"content_type\":\"json\",\"insecure_ssl\":\"1\"}}";

    // Create the entity
    HttpEntity<String> entity = new HttpEntity<>(body, headers);

    // Make the request using the github api, in the future we can make api dynamic to use github server url
    ResponseEntity<String> response = restTemplate.exchange("https://api.github.com/repos/" + ownerAndRepo+"/hooks", HttpMethod.POST, entity, String.class);

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

private String extractOwnerAndRepo(String repoUrl) {
    try {
        URL url = new URL(repoUrl);
        String[] parts = url.getPath().split("/");
        String owner = parts[1];
        String repo = parts[2].replace(".git", "");
        return owner + "/" + repo;
    } catch (Exception e) {
       log.error("error extracing the repo", e);;
    }
    return "";
}

private static String bytesToHex(byte[] hash) {
    StringBuilder hexString = new StringBuilder(2 * hash.length);
    for (byte b : hash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
            hexString.append('0');
        }
        hexString.append(hex);
    }
    return hexString.toString();
}

}
