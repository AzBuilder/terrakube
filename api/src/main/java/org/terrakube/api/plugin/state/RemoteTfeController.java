package org.terrakube.api.plugin.state;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.terrakube.api.plugin.state.model.organization.EntitlementModel;
import org.terrakube.api.plugin.state.model.organization.OrganizationModel;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/remote/tfe/v2/")
@AllArgsConstructor
public class RemoteTfeController {

    RemoteTfeService remoteTfeService;

    @GetMapping(produces = "application/vnd.api+json", path = "organizations/{organizationName}/entitlement-set")
    public ResponseEntity<EntitlementModel> getOrgEntitlementSet(@PathVariable("organizationName") String organizationName) {
        return ResponseEntity.of(Optional.ofNullable(remoteTfeService.getOrgEntitlementSet(organizationName)));
    }

    @GetMapping(produces = "application/vnd.api+json", path = "organizations/{organizationName}")
    public ResponseEntity<OrganizationModel> getOrgInformation(@PathVariable("organizationName") String organizationName) {
        return ResponseEntity.of(Optional.ofNullable(remoteTfeService.getOrgInformation(organizationName)));
    }

    @GetMapping (produces = "application/vnd.api+json", path = "organizations/{organization}/workspaces/{workspaceName}")
    public ResponseEntity<String> terraformJson3(@PathVariable("organizationName") String organizationName) {
        return ResponseEntity.ok("response3");
    }

    @PostMapping(produces = "application/vnd.api+json", path = "organizations/{organization}/workspaces")
    public ResponseEntity<String> terraformJson4(@PathVariable("organizationName") String organizationName) {
        return ResponseEntity.ok("response3");
    }
}
