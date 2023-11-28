package org.terrakube.api.plugin.vcs;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

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

    protected boolean verifySignature(Map<String, String> headers, String headerName, String token, String jsonPayload) {
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

}
