package org.terrakube.api.plugin.vcs.provider.github;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.terrakube.api.rs.vcs.GitHubAppToken;
import org.terrakube.api.rs.vcs.Vcs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
// The service that actually does the work of generating and refreshing access tokens for the GitHub App
public class GitHubAppTokenService {
    private final ObjectMapper objectMapper;

    // Refreshes the access token for all existing installations of the app
    public List<GitHubAppToken> generateAccessToken(Vcs vcs) throws JsonMappingException, JsonProcessingException, NoSuchAlgorithmException, InvalidKeySpecException {
        ArrayList<GitHubAppToken> gitHubAppTokens = new ArrayList<>();
        String jws = generateJWT(vcs.getClientId(), vcs.getPrivateKey());

        String apiUrl = vcs.getApiUrl() + "/app/installations";
        String installationId;
        ResponseEntity<String> response = callGithubAPI("", apiUrl, HttpMethod.GET, jws);
        if (response.getStatusCode().value() == 200) {
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            for (JsonNode node: rootNode) {
                GitHubAppInstallationModel installation = objectMapper.treeToValue(node, GitHubAppInstallationModel.class);
                installationId = installation.getId();
                GitHubAppToken gitHubAppToken = new GitHubAppToken();
                gitHubAppToken.setId(UUID.randomUUID());
                gitHubAppToken.setInstallationId(installationId);
                gitHubAppToken.setVcs(vcs);
                gitHubAppToken.setOwner(installation.getAccount().getLogin());
                gitHubAppToken.setToken(getAccessToken(installationId, vcs.getApiUrl(), jws, installation.getAccount().getLogin()));
                gitHubAppTokens.add(gitHubAppToken);
            }
        }
        return gitHubAppTokens;
    }

    // Generates a new access token for a specific installation of the app that hasn't been saved in the GitHubAppToken table yet
    public GitHubAppToken generateAccessToken(Vcs vcs, String[] ownerAndRepo) throws JsonMappingException, JsonProcessingException, NoSuchAlgorithmException, InvalidKeySpecException {
        String jws = generateJWT(vcs.getClientId(), vcs.getPrivateKey());
        GitHubAppToken gitHubAppToken = getAccessToken(ownerAndRepo, vcs.getApiUrl(), jws);
        gitHubAppToken.setVcs(vcs);
        return gitHubAppToken;
    }

    // Refreshes the access token for a specific installation of the app that's already been saved in the GitHubAppToken table
    public String refreshAccessToken(GitHubAppToken gitHubAppToken)
            throws NoSuchAlgorithmException, InvalidKeySpecException, JsonMappingException, JsonProcessingException {
        Vcs vcs = gitHubAppToken.getVcs();
        String jws = generateJWT(vcs.getClientId(), vcs.getPrivateKey());
        return getAccessToken(gitHubAppToken.getInstallationId(), vcs.getApiUrl(), jws, gitHubAppToken.getOwner());
    }
    
    // Gets the access token with owner and repo for a specific installation of the app
    private GitHubAppToken getAccessToken(String[] ownerAndRepo, String vcsApiUrl, String jws)
            throws JsonMappingException, JsonProcessingException {
        GitHubAppToken gitHubAppToken= new GitHubAppToken();
        String url = vcsApiUrl + "/repos/" + String.join("/", ownerAndRepo) + "/installation";
        log.debug("Getting access token for user/organization {}", ownerAndRepo[0]);
        ResponseEntity<String> tokenResponse = callGithubAPI("", url, HttpMethod.GET, jws);
        if (tokenResponse.getStatusCode().value() == 200) {
            JsonNode rootNode = objectMapper.readTree(tokenResponse.getBody());
            String installationId = rootNode.path("id").asText();
            gitHubAppToken.setId(UUID.randomUUID());
            gitHubAppToken.setInstallationId(installationId);
            gitHubAppToken.setOwner(ownerAndRepo[0]);
            gitHubAppToken.setToken(getAccessToken(installationId, vcsApiUrl, jws, ownerAndRepo[0]));
        }

        return gitHubAppToken;
    }

    // Gets the access token with app installation ID for a specific installation of the app
    private String getAccessToken(String installationId, String vcsApiUrl, String jws, String owner)
            throws JsonMappingException, JsonProcessingException {
        String token = "";
        String url = vcsApiUrl + "/app/installations/" + installationId + "/access_tokens";
        log.debug("Getting access token for installation {} on user/organization {}", installationId, owner);
        ResponseEntity<String> tokenResponse = callGithubAPI("", url, HttpMethod.POST, jws);
        if (tokenResponse.getStatusCode().value() == 201) {
            token = objectMapper.readTree(tokenResponse.getBody()).path("token").asText();
        }
        return token;
    }

    // Generates a JWT token for the GitHub App
    private String generateJWT(String clientId, String privateKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String keyPem = privateKey.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "");

        log.debug("Stripped PKCS8 private key starting with {} and ending with {}", keyPem.substring(0, 10),
                keyPem.substring(keyPem.length() - 10));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(keyPem));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey key = keyFactory.generatePrivate(keySpec);

        Instant now = Instant.now();
        String jws = Jwts.builder()
                .setIssuer(clientId)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(10, ChronoUnit.MINUTES)))
                .signWith(key, SignatureAlgorithm.RS256)
                .compact();
        return jws;
    }

    // Calls the GitHub API
    private ResponseEntity<String> callGithubAPI(String body, String apiUrl, HttpMethod method, String jws) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.github+json");
        headers.set("Authorization", "Bearer " + jws);
        headers.set("X-GitHub-Api-Version", "2022-11-28");
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(apiUrl, method, entity, String.class);
    }
}