package org.terrakube.api.plugin.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.terrakube.api.repository.GlobalVarRepository;
import org.terrakube.api.repository.VariableRepository;
import org.terrakube.api.repository.WorkspaceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import org.terrakube.api.rs.Organization;
import org.terrakube.api.rs.workspace.Workspace;
import org.terrakube.api.rs.workspace.parameters.Variable;
import org.terrakube.api.rs.globalvar.Globalvar;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/proxy/v1")
public class ProxyController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WorkspaceRepository workspaceRepository;
    private final VariableRepository variableRepository;
    private final GlobalVarRepository globalVarRepository;

    public ProxyController(WorkspaceRepository workspaceRepository, VariableRepository variableRepository, GlobalVarRepository globalVarRepository) {
        this.workspaceRepository = workspaceRepository;
        this.variableRepository = variableRepository;
        this.globalVarRepository = globalVarRepository;
    }

    private static final Map<String, String> VARS = new HashMap<>();

    @GetMapping("/**")
    public ResponseEntity<String> proxyGetRequest(RequestEntity<String> requestEntity, @RequestParam("targetUrl") String targetUrl, @RequestParam(value = "proxyheaders", required = false) String proxyHeaders, @RequestParam("workspaceId") UUID workspaceId) {
        return proxyRequest(requestEntity, targetUrl, proxyHeaders, workspaceId);
    }

    @PostMapping(value = "/**", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<String> proxyPostRequest(RequestEntity<String> requestEntity, @RequestParam("targetUrl") String targetUrl, @RequestParam(value = "proxyheaders", required = false) String proxyHeaders, @RequestParam("workspaceId") UUID workspaceId) {
        return proxyRequest(requestEntity, targetUrl, proxyHeaders, workspaceId);
    }

    @PutMapping("/**")
    public ResponseEntity<String> proxyPutRequest(RequestEntity<String> requestEntity, @RequestParam("targetUrl") String targetUrl, @RequestParam(value = "proxyheaders", required = false) String proxyHeaders, @RequestParam("workspaceId") UUID workspaceId) {
        return proxyRequest(requestEntity, targetUrl, proxyHeaders, workspaceId);
    }

    @RequestMapping(value = "/**", method = RequestMethod.DELETE)
    public ResponseEntity<String> proxyDeleteRequest(RequestEntity<String> requestEntity, @RequestParam("targetUrl") String targetUrl, @RequestParam(value = "proxyheaders", required = false) String proxyHeaders, @RequestParam("workspaceId") UUID workspaceId) {
        return proxyRequest(requestEntity, targetUrl, proxyHeaders, workspaceId);
    }

    @PatchMapping("/**")
    public ResponseEntity<String> proxyPatchRequest(RequestEntity<String> requestEntity, @RequestParam("targetUrl") String targetUrl, @RequestParam(value = "proxyheaders", required = false) String proxyHeaders, @RequestParam("workspaceId") UUID workspaceId) {
        return proxyRequest(requestEntity, targetUrl, proxyHeaders, workspaceId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private ResponseEntity<String> proxyRequest(RequestEntity<String> requestEntity, String targetUrl, String proxyHeadersJson, UUID workspaceId) {
        HttpMethod method = requestEntity.getMethod();
        HttpHeaders headers = new HttpHeaders();

        // Fetch workspace and variables
        fetchWorkspaceVars(workspaceId);

        // Replace variables in targetUrl
        targetUrl = replaceVars(targetUrl);

        // Add custom headers
        if (proxyHeadersJson != null) {
            try {
                Map<String, String> customHeaders = objectMapper.readValue(proxyHeadersJson, Map.class);
                customHeaders.forEach((key, value) -> headers.set(key, replaceVars(value)));
            } catch (Exception e) {
                log.error("Error parsing proxyheaders JSON: ", e);
            }
        }

        String body = requestEntity.getBody();
        if (body != null) {
            try {
                // Decode the body
                String decodedBody = URLDecoder.decode(body, StandardCharsets.UTF_8);

                // Extract proxyBody if present
                JsonNode jsonNode = objectMapper.readTree(decodedBody);
                JsonNode proxyBodyNode = jsonNode.get("proxyBody");
                if (proxyBodyNode != null) {
                    String proxyBodyString = proxyBodyNode.asText();
                    // Replace variables in the proxy body
                    String replacedBody = replaceVars(proxyBodyString);

                    // Reassign the processed body
                    body = replacedBody;
                }
            } catch (Exception e) {
                log.error("Error processing request body: ", e);
            }
        }

        // Log headers and body for debugging
        log.info("Request Headers: {}", headers);
        log.info("Request Body: {}", body);
        log.info("Target URL: {}", targetUrl);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(targetUrl, method, entity, String.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            log.error("Error forwarding request to {}: ", targetUrl, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error forwarding request");
        }
    }

    @Transactional
    private void fetchWorkspaceVars(UUID workspaceId) {
        VARS.clear();
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(() -> new IllegalArgumentException("Invalid workspace ID"));
        Organization organization = workspace.getOrganization();

        List<Globalvar> globalVariables = globalVarRepository.findByOrganization(organization);
        globalVariables.forEach(globalvar -> VARS.put(globalvar.getKey(), globalvar.getValue()));

        List<Variable> variables = variableRepository.findByWorkspace(workspace);
        variables.forEach(variable -> VARS.put(variable.getKey(), variable.getValue()));
    }

    private String replaceVars(String input) {
        if (input == null) {
            return null;
        }

        Pattern pattern = Pattern.compile("\\{\\{var\\.([a-zA-Z0-9_]+)\\}\\}");
        Matcher matcher = pattern.matcher(input);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String replacement = VARS.getOrDefault(matcher.group(1), matcher.group(0));
            matcher.appendReplacement(buffer, replacement);
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}