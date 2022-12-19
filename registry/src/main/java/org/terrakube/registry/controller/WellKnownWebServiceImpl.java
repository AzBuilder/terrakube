package org.terrakube.registry.controller;

import org.terrakube.registry.configuration.OpenRegistryProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/.well-known/terraform.json")
public class WellKnownWebServiceImpl {

    private static final String terraformJsonContent = "{\n" +
            "  \"modules.v1\": \"%s/terraform/modules/v1/\"\n," +
            "  \"providers.v1\": \"%s/terraform/providers/v1/\"," +
            "  \"login.v1\": {\n" +
            "    \"client\": \"%s\",\n" +
            "    \"grant_types\": [\"authz_code\", \"openid\", \"profile\", \"email\", \"offline_access\", \"groups\"],\n" +
            "    \"authz\": \"%s/auth?scope=openid+profile+email+offline_access+groups\",\n" +
            "    \"token\": \"%s/token\",\n" +
            "    \"ports\": [10000, 10001]\n" +
            "    },\n"+
            "  \"state.v2\": \"%s/terrakube/state/v1/\"\n," +
            "  \"tfe.v2.1\": \"%s/terrakube/tfe/v1/\"\n" +
            "}";

    @Autowired
    OpenRegistryProperties openRegistryProperties;

    @GetMapping(produces = "application/json")
    public ResponseEntity<String> terraformJson() {
        return ResponseEntity.ok(
                String.format(terraformJsonContent,
                        openRegistryProperties.getHostname(),
                        openRegistryProperties.getHostname(),
                        openRegistryProperties.getClientId(),
                        openRegistryProperties.getIssuerUri(),
                        openRegistryProperties.getIssuerUri(),
                        openRegistryProperties.getHostname(),
                        openRegistryProperties.getHostname()
                )
        );
    }
}
