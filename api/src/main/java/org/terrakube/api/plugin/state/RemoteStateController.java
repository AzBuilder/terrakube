package org.terrakube.api.plugin.state;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpEntity;

import java.util.HashMap;
import java.util.Optional;

@Slf4j
//@RestController
//@RequestMapping("/remote/state/v2/")
@AllArgsConstructor
public class RemoteStateController {

    RemoteTfeService remoteTfeService;

    //@Transactional
    //@PostMapping(produces = "application/vnd.api+json", path = "/workspaces/{workspaceId}/state-versions")
    public ResponseEntity<?> createWorkspaceState(@PathVariable("workspaceId") String workspaceId, HttpEntity<String> httpEntity) {
        log.info("Create State /remote/state/v2/ {}", workspaceId);
        log.info("{}", httpEntity.getBody());
        
        // CLI is sending {"data":{"type":"workspaces","attributes":{"name":"workspace1","terraform-version":"1.1.3"}}}
        return ResponseEntity.status(201).body(        "{\n" +
        "    \"data\": {\n" +
        "        \"id\": \"sv-DmoXecHePnNznaA4\",\n" +
        "        \"type\": \"state-versions\",\n" +
        "        \"attributes\": {\n" +
        "            \"vcs-commit-sha\": null,\n" +
        "            \"vcs-commit-url\": null,\n" +
        "            \"created-at\": \"2018-07-12T20:32:01.490Z\",\n" +
        "            \"hosted-state-download-url\": \"https://archivist.terraform.io/v1/object/f55b739b-ff03-4716-b436-726466b96dc4\",\n" +
        "            \"hosted-json-state-download-url\": \"https://archivist.terraform.io/v1/object/4fde7951-93c0-4414-9a40-f3abc4bac490\",\n" +
        "            \"serial\": 1\n" +
        "        },\n" +
        "        \"links\": {\n" +
        "            \"self\": \"/api/v2/state-versions/sv-DmoXecHePnNznaA4\"\n" +
        "        }\n" +
        "    }\n" +
        "}");
    }
}
