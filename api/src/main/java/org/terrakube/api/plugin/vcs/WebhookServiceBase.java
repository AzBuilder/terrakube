package org.terrakube.api.plugin.vcs;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.datical.liquibase.ext.checks.config.TriFunction;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.function.BiFunction;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@AllArgsConstructor
@Slf4j
@Service
public class WebhookServiceBase {

    protected String extractOwnerAndRepo(String repoUrl) {
        try {
            URL url = new URL(repoUrl);
            String[] parts = url.getPath().split("/");
            String owner = parts[1];
            String repo = parts[2].replace(".git", "");
            return owner + "/" + repo;
        } catch (Exception e) {
            log.error("error extracing the repo", e);
            return "";
        }
    }

    protected static String bytesToHex(byte[] hash) {
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

    protected boolean verifySignature(Map<String, String> headers, String headerName, String token,
            String jsonPayload) {
        try {
            String signatureHeader = headers.get(headerName);
            if (signatureHeader == null) {
                log.error(headerName + " header is missing!");
                return false;
            }
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(token.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
            mac.init(secretKeySpec);
            byte[] computedHash = mac.doFinal(jsonPayload.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = "sha256=" + bytesToHex(computedHash);

            if (!signatureHeader.equals(expectedSignature)) {
                log.error("Request signature didn't match!");
                return false;
            }
            return true;
        } catch (NoSuchAlgorithmException e) {
            log.info("Error processing the webhook", e);
            return false;
        } catch (InvalidKeyException e) {
            log.info("Error parsing the secret", e);
            return false;
        }

    }

    protected ResponseEntity<String> makeApiRequest(HttpHeaders headers, String body, String apiUrl) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
    }

    protected WebhookResult handleWebhook(String jsonPayload, Map<String, String> headers, String token,
            String signatureHeader, String via, TriFunction<String, WebhookResult, Map<String, String>, WebhookResult > handleEvent) {
        WebhookResult result = new WebhookResult();
        result.setBranch("");
        result.setVia(via);
        result.setWorkspaceId(Base64.getDecoder().decode(token).toString());

        log.info("verify signature for " + via + " webhook");
        result.setValid(verifySignature(headers, signatureHeader, token, jsonPayload));

        if (!result.isValid()) {
            log.info("Signature verification failed");
            return result;
        }

        log.info("Parsing " + via + " webhook payload");

        try {
            result = handleEvent.apply(jsonPayload, result,headers);
        } catch (Exception e) {
            log.info("Error processing the webhook", e);
        }

        return result;
    }

}
