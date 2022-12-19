package org.terrakube.api.plugin.state;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.terrakube.api.plugin.state.model.entitlement.EntitlementData;
import org.terrakube.api.plugin.state.model.entitlement.EntitlementModel;
import org.terrakube.api.plugin.state.model.organization.OrganizationModel;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/remote/tfe/v2/")
@AllArgsConstructor
public class RemoteTfeController {

    RemoteTfeService remoteTfeService;

    @GetMapping(produces = "application/vnd.api+json", path = "organizations/{organizationName}/entitlement-set")
    public ResponseEntity<EntitlementData> getOrgEntitlementSet(@PathVariable("organizationName") String organizationName) {
        return ResponseEntity.of(Optional.ofNullable(remoteTfeService.getOrgEntitlementSet(organizationName)));
    }

    @GetMapping(produces = "application/vnd.api+json", path = "organizations/{organizationName}")
    public ResponseEntity<OrganizationModel> getOrgInformation(@PathVariable("organizationName") String organizationName) {
        return ResponseEntity.of(Optional.ofNullable(remoteTfeService.getOrgInformation(organizationName)));
    }

    @GetMapping (produces = "application/vnd.api+json", path = "organizations/{organization}/workspaces/{workspaceName}")
    public ResponseEntity<?> terraformJson3(@PathVariable("organizationName") String organizationName, @PathVariable("workspaceName") String workspaceName) {
        log.info("Searching: {} {}", organizationName, workspaceName);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(produces = "application/vnd.api+json", path = "organizations/{organization}/workspaces")
    public ResponseEntity<String> terraformJson4(@PathVariable("organizationName") String organizationName, HttpEntity<String> httpEntity) {
        log.info("Body create \n {}", httpEntity.getBody());
        String response = "{\n" +
                "  \"data\": {\n" +
                "    \"attributes\": {\n" +
                "      \"name\": \"workspace1\",\n" +
                "      \"resource-count\": 0,\n" +
                "      \"updated-at\": \"2017-11-29T19:18:09.976Z\"\n" +
                "    },\n" +
                "    \"id\": \"12412412341234\"\n," +
                "    \"type\": \"workspaces\"\n" +
                "  }\n" +
                "}";
        return ResponseEntity.status(201).body(response);
    }
}
