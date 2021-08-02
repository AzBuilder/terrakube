package org.azbuilder.registry.controller;

import org.azbuilder.registry.configuration.OpenRegistryProperties;
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
            "  \"providers.v1\": \"%s/terraform/providers/v1/\"" +
            "}";

    @Autowired
    OpenRegistryProperties openRegistryProperties;

    @GetMapping(produces = "application/json")
    public ResponseEntity<String> terraformJson() {
        return ResponseEntity.ok(String.format(terraformJsonContent, openRegistryProperties.getHostname(), openRegistryProperties.getHostname()));
    }
}
