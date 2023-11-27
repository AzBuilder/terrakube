package org.terrakube.api.plugin.vcs;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.net.URL;

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

}
