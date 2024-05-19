package org.terrakube.api.plugin.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequestMapping("/proxy/v1")
public class ProxyController {

    private final RestTemplate restTemplate = new RestTemplate();

    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
    public ResponseEntity<String> proxyRequest(RequestEntity<String> requestEntity, @RequestParam("targetUrl") String targetUrl) {
        HttpMethod method = requestEntity.getMethod();
        HttpHeaders headers = requestEntity.getHeaders();
        String body = requestEntity.getBody();

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(targetUrl, method, entity, String.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            log.error("Error forwarding request to {}: ", targetUrl, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error forwarding request");
        }
    }
}
