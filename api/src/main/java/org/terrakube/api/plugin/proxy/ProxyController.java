package org.terrakube.api.plugin.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/proxy/v1")
public class ProxyController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Hardcoded map for global variables
    private static final Map<String, String> GLOBAL_VARS = new HashMap<>();
    static {
        GLOBAL_VARS.put("ARM_TENANT_ID", "");
        GLOBAL_VARS.put("CLIENT_ID", "");
        GLOBAL_VARS.put("CLIENT_SECRET", "");
    }

    @RequestMapping(value = "/**", method = RequestMethod.GET)
    public ResponseEntity<String> proxyGetRequest(RequestEntity<String> requestEntity, @RequestParam("targetUrl") String targetUrl, @RequestParam(value = "proxyheaders", required = false) String proxyHeaders) {
        return proxyRequest(requestEntity, targetUrl, proxyHeaders);
    }

    @RequestMapping(value = "/**", method = RequestMethod.POST)
    public ResponseEntity<String> proxyPostRequest(RequestEntity<String> requestEntity, @RequestParam("targetUrl") String targetUrl, @RequestParam(value = "proxyheaders", required = false) String proxyHeaders) {
        return proxyRequest(requestEntity, targetUrl, proxyHeaders);
    }

    @RequestMapping(value = "/**", method = RequestMethod.PUT)
    public ResponseEntity<String> proxyPutRequest(RequestEntity<String> requestEntity, @RequestParam("targetUrl") String targetUrl, @RequestParam(value = "proxyheaders", required = false) String proxyHeaders) {
        return proxyRequest(requestEntity, targetUrl, proxyHeaders);
    }

    @RequestMapping(value = "/**", method = RequestMethod.DELETE)
    public ResponseEntity<String> proxyDeleteRequest(RequestEntity<String> requestEntity, @RequestParam("targetUrl") String targetUrl, @RequestParam(value = "proxyheaders", required = false) String proxyHeaders) {
        return proxyRequest(requestEntity, targetUrl, proxyHeaders);
    }

    @RequestMapping(value = "/**", method = RequestMethod.PATCH)
    public ResponseEntity<String> proxyPatchRequest(RequestEntity<String> requestEntity, @RequestParam("targetUrl") String targetUrl, @RequestParam(value = "proxyheaders", required = false) String proxyHeaders) {
        return proxyRequest(requestEntity, targetUrl, proxyHeaders);
    }

    private ResponseEntity<String> proxyRequest(RequestEntity<String> requestEntity, String targetUrl, String proxyHeadersJson) {
        HttpMethod method = requestEntity.getMethod();
        HttpHeaders headers = new HttpHeaders();

        // Replace global variables in targetUrl
        targetUrl = replaceGlobalVars(targetUrl);

        // Add custom headers
        if (proxyHeadersJson != null) {
            try {
                Map<String, String> customHeaders = objectMapper.readValue(proxyHeadersJson, Map.class);
                customHeaders.forEach((key, value) -> headers.set(key, replaceGlobalVars(value)));
            } catch (Exception e) {
                log.error("Error parsing proxyheaders JSON: ", e);
            }
        }

        String body = requestEntity.getBody();
        if (body != null) {
            // Decode the body
            String decodedBody = URLDecoder.decode(body, StandardCharsets.UTF_8);

            // Replace global variables in the decoded body
            String replacedBody = replaceGlobalVars(decodedBody);

            body = replacedBody;
        }

        // Log headers and body for debugging
        log.debug("Request Headers: {}", headers);
        log.debug("Request Body: {}", body);
        log.debug("Target URL: {}", targetUrl);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(targetUrl, method, entity, String.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            log.error("Error forwarding request to {}: ", targetUrl, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error forwarding request");
        }
    }

    private String replaceGlobalVars(String input) {
        if (input == null) {
            return null;
        }

        Pattern pattern = Pattern.compile("\\{\\{globalvar\\.([a-zA-Z0-9_]+)\\}\\}");
        Matcher matcher = pattern.matcher(input);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String replacement = GLOBAL_VARS.getOrDefault(matcher.group(1), matcher.group(0));
            matcher.appendReplacement(buffer, replacement);
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}