package org.terrakube.api.plugin.state;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/.well-known/terraform.json")
public class WellKnownWebServiceImpl {

    private static final String terraformJsonContent = "{\n" +
            "  \"state.v2\": \"%s/terrakube/state/v1/\"\n," +
            "  \"tfe.v2.1\": \"%s/terrakube/tfe/v1/\"\n," +
            "}";

    @Value("${org.terrakube.hostname}s")
    String hostname;

    @GetMapping(produces = "application/json")
    public ResponseEntity<String> terraformJson() {
        return ResponseEntity.ok(
                String.format(terraformJsonContent,hostname)
        );
    }
}
