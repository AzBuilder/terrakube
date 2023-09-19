package org.terrakube.api.plugin.state;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.terrakube.api.plugin.state.model.configuration.ConfigurationData;
import org.terrakube.api.plugin.state.model.entitlement.EntitlementData;
import org.terrakube.api.plugin.state.model.organization.OrganizationData;
import org.terrakube.api.plugin.state.model.plan.PlanRunData;
import org.terrakube.api.plugin.state.model.apply.ApplyRunData;
import org.terrakube.api.plugin.state.model.runs.RunsData;
import org.terrakube.api.plugin.state.model.state.StateData;
import org.terrakube.api.plugin.state.model.workspace.WorkspaceData;
import org.terrakube.api.plugin.state.model.workspace.WorkspaceList;
import org.terrakube.api.plugin.state.model.workspace.state.consumers.StateConsumerList;
import org.terrakube.api.plugin.state.model.workspace.tags.TagDataList;

import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/remote/tfe/v2/")
@AllArgsConstructor
public class RemoteTfeController {

    RemoteTfeService remoteTfeService;

    @Transactional
    @GetMapping(produces = "application/vnd.api+json", path = "organizations/{organizationName}/entitlement-set")
    public ResponseEntity<EntitlementData> getOrgEntitlementSet(@PathVariable("organizationName") String organizationName, Principal principal) {
        return ResponseEntity.of(Optional.ofNullable(remoteTfeService.getOrgEntitlementSet(organizationName, ((JwtAuthenticationToken) principal))));
    }

    @GetMapping(produces = "application/vnd.api+json", path = "ping")
    public ResponseEntity<String> ping() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("TFP-API-Version", "2.5");
        responseHeaders.set("TFP-AppName", "Terrakube");
        ResponseEntity response = new ResponseEntity<>(responseHeaders, HttpStatus.NOT_FOUND);
        return response;
    }

    @Transactional
    @GetMapping(produces = "application/vnd.api+json", path = "organizations/{organizationName}")
    public ResponseEntity<OrganizationData> getOrgInformation(@PathVariable("organizationName") String organizationName, Principal principal) {
        return ResponseEntity.of(Optional.ofNullable(remoteTfeService.getOrgInformation(organizationName, (JwtAuthenticationToken) principal)));
    }

    @GetMapping(produces = "application/vnd.api+json", path = "organizations/{organizationName}/workspaces/{workspaceName}")
    public ResponseEntity<WorkspaceData> getWorkspace(@PathVariable("organizationName") String organizationName, @PathVariable("workspaceName") String workspaceName, Principal principal) {
        log.info("Searching: {} {}", organizationName, workspaceName);
        return ResponseEntity.of(Optional.ofNullable(remoteTfeService.getWorkspace(organizationName, workspaceName, new HashMap<>(), (JwtAuthenticationToken) principal)));
    }

    @Transactional
    @GetMapping(produces = "application/vnd.api+json", path = "workspaces/{workspaceId}/relationships/remote-state-consumers")
    public ResponseEntity<StateConsumerList> getWorkspaceStateConsumers(@PathVariable("workspaceId") String workspaceId, Principal principal) {
        log.info("Searching Workspace Consumers for Id: {}", workspaceId);
        return ResponseEntity.of(Optional.ofNullable(remoteTfeService.getWorkspaceStateConsumers(workspaceId, (JwtAuthenticationToken) principal)));
    }

    @Transactional
    @GetMapping(produces = "application/vnd.api+json", path = "organizations/{organizationName}/workspaces")
    public ResponseEntity<WorkspaceList> listWorkspace(@PathVariable("organizationName") String organizationName, @RequestParam("search[tags]") String searchTags, Principal principal) {
        log.info("Searching: {} {}", organizationName, searchTags);
        return ResponseEntity.of(Optional.ofNullable(remoteTfeService.listWorkspace(organizationName, searchTags, (JwtAuthenticationToken) principal)));
    }

    @Transactional
    @PostMapping(produces = "application/vnd.api+json", path = "/workspaces/{workspaceId}/relationships/tags")
    public ResponseEntity<String> updateWorkspaceTags(@PathVariable("workspaceId") String workspaceId, @RequestBody TagDataList tagDataList) {
        log.info("Updating Workspace Tags {}", tagDataList.toString());
        boolean workspaceTags = remoteTfeService.updateWorkspaceTags(workspaceId, tagDataList);
        if (workspaceTags) {
            return ResponseEntity.status(204).body("");
        } else {
            return ResponseEntity.status(404).body("");
        }
    }


    @PostMapping(produces = "application/vnd.api+json", path = "organizations/{organizationName}/workspaces")
    public ResponseEntity<WorkspaceData> createWorkspace(@PathVariable("organizationName") String organizationName, @RequestBody WorkspaceData workspaceData, Principal principal) {
        log.info("Create {}", workspaceData.toString());
        Optional<WorkspaceData> newWorkspace = Optional.ofNullable(remoteTfeService.createWorkspace(organizationName, workspaceData,(JwtAuthenticationToken) principal));
        if (newWorkspace.isPresent()) {
            log.info("Created: {}", newWorkspace.get().toString());
            return ResponseEntity.status(201).body(newWorkspace.get());
        } else {
            return ResponseEntity.status(500).body(new WorkspaceData());
        }
    }

    @Transactional
    @PatchMapping(produces = "application/vnd.api+json", path = "workspaces/{workspacesId}")
    public ResponseEntity<WorkspaceData> updateWorkspace(@PathVariable("workspacesId") String workspacesId, @RequestBody WorkspaceData workspaceData, Principal principal) {
        log.info("Create {}", workspaceData.toString());
        Optional<WorkspaceData> updatedWorkspace = Optional.ofNullable(remoteTfeService.updateWorkspace(workspacesId, workspaceData, (JwtAuthenticationToken) principal));
        if (updatedWorkspace.isPresent()) {
            log.info("Created: {}", updatedWorkspace.get().toString());
            return ResponseEntity.status(201).body(updatedWorkspace.get());
        } else {
            return ResponseEntity.status(500).body(new WorkspaceData());
        }
    }

    @Transactional
    @PostMapping(produces = "application/vnd.api+json", path = "/workspaces/{workspaceId}/actions/lock")
    public ResponseEntity<WorkspaceData> lockWorkspace(@PathVariable("workspaceId") String workspaceId, Principal principal) {
        log.info("Lock {}", workspaceId);
        return ResponseEntity.of(Optional.ofNullable(remoteTfeService.updateWorkspaceLock(workspaceId, true, (JwtAuthenticationToken) principal)));
    }

    @Transactional
    @PostMapping(produces = "application/vnd.api+json", path = "/workspaces/{workspaceId}/actions/unlock")
    public ResponseEntity<WorkspaceData> unlockWorkspace(@PathVariable("workspaceId") String workspaceId, Principal principal) {
        log.info("Unlock {}", workspaceId);
        return ResponseEntity.of(Optional.ofNullable(remoteTfeService.updateWorkspaceLock(workspaceId, false, (JwtAuthenticationToken) principal)));
    }

    @Transactional
    @PostMapping(produces = "application/vnd.api+json", path = "/workspaces/{workspaceId}/state-versions")
    public ResponseEntity<StateData> createWorkspaceState(@PathVariable("workspaceId") String workspaceId, @RequestBody StateData stateData) {
        log.info("Create State /remote/tfe/v2/ {}", workspaceId);
        log.info("Body: {}", stateData.toString());
        return ResponseEntity.of(Optional.of(remoteTfeService.createWorkspaceState(workspaceId, stateData)));
    }

    @Transactional
    @GetMapping(produces = "application/vnd.api+json", path = "/workspaces/{workspaceId}/current-state-version")
    public ResponseEntity<StateData> getCurrentWorkspaceState(@PathVariable("workspaceId") String workspaceId) {
        log.info("Get current workspace state {}", workspaceId);
        return ResponseEntity.of(Optional.ofNullable(remoteTfeService.getCurrentWorkspaceState(workspaceId)));
    }

    @Transactional
    @PostMapping(produces = "application/vnd.api+json", path = "/workspaces/{workspaceId}/configuration-versions")
    public ResponseEntity<ConfigurationData> createConfigurationVersion(@PathVariable("workspaceId") String workspaceId, @RequestBody ConfigurationData configurationData) {
        log.info("Creating Configuration Version for worspaceId {}", workspaceId);
        return ResponseEntity.status(201).body(remoteTfeService.createConfigurationVersion(workspaceId, configurationData));
    }

    @Transactional
    @GetMapping(produces = "application/vnd.api+json", path = "/configuration-versions/{configurationId}")
    public ResponseEntity<ConfigurationData> getConfigurationVersion(@PathVariable("configurationId") String configurationId) {
        log.info("Searching Configuration Version Id {}", configurationId);
        return ResponseEntity.of(Optional.ofNullable(remoteTfeService.searchConfiguration(configurationId)));
    }

    @Transactional
    @PutMapping(path = "/configuration-versions/{configurationid}")
    public ResponseEntity<String> uploadConfiguration(HttpServletRequest httpServletRequest, @PathVariable("configurationid") String configurationId) throws IOException {
        log.info("Uploading Id {} file", configurationId);
        remoteTfeService.uploadFile(configurationId, httpServletRequest.getInputStream());
        log.info("File created");
        return ResponseEntity.ok().body("");
    }

    @Transactional
    @PostMapping(produces = "application/vnd.api+json", path = "/runs")
    public ResponseEntity<RunsData> createRun(@RequestBody RunsData runsData) throws SchedulerException, ParseException {
        log.info("Create new run");
        return ResponseEntity.status(201).body(remoteTfeService.createRun(runsData));
    }

    @GetMapping(produces = "application/vnd.api+json", path = "/runs/{runsId}/run-events")
    public ResponseEntity<String> getRunEvents(@PathVariable("runsId") String runsId) {
        log.info("Gettinng Runs Events for Run Id {}", runsId);
        return ResponseEntity.status(200).body("{\n" +
                "  \"data\": []\n" +
                "}");
    }

    @Transactional
    @GetMapping(produces = "application/vnd.api+json", path = "/runs/{runId}")
    public ResponseEntity<RunsData> getRun(@PathVariable("runId") int runId, @RequestParam(name = "include", required = false) String include) {
        return ResponseEntity.ok(remoteTfeService.getRun(runId, include));
    }

    @Transactional
    @PostMapping(produces = "application/vnd.api+json", path = "/runs/{runId}/actions/apply")
    public ResponseEntity<RunsData> runApply(@PathVariable("runId") int runId) {
        return ResponseEntity.ok(remoteTfeService.runApply(runId));
    }

    @Transactional
    @PostMapping(produces = "application/vnd.api+json", path = "/runs/{runId}/actions/discard")
    public ResponseEntity<RunsData> runDiscard(@PathVariable("runId") int runId) {
        return ResponseEntity.ok(remoteTfeService.runDiscard(runId));
    }


    @Transactional
    @GetMapping(produces = "application/vnd.api+json", path = "/plans/{planId}")
    public ResponseEntity<PlanRunData> getPlan(@PathVariable("planId") int planId) {
        return ResponseEntity.ok(remoteTfeService.getPlan(planId));
    }

    @Transactional
    @GetMapping(produces = "application/vnd.api+json", path = "/applies/{applyId}")
    public ResponseEntity<ApplyRunData> getApply(@PathVariable("applyId") int applyId) {
        return ResponseEntity.ok(remoteTfeService.getApply(applyId));
    }

    @Transactional
    @GetMapping(produces = "application/vnd.api+json", path = "/plans/{planId}/logs")
    public ResponseEntity<String> getPlanLogs(@PathVariable("planId") int planId) throws IOException {
        return ResponseEntity.of(Optional.ofNullable(new String(remoteTfeService.getPlanLogs(planId), StandardCharsets.UTF_8)));
    }

    @Transactional
    @GetMapping(produces = "application/vnd.api+json", path = "/applies/{applyId}/logs")
    public ResponseEntity<String> getApplyLogs(@PathVariable("applyId") int planId) throws IOException {
        return ResponseEntity.of(Optional.ofNullable(new String(remoteTfeService.getApplyLogs(planId), StandardCharsets.UTF_8)));
    }

    @GetMapping(
            value = "configuration-versions/{planId}/terraformContent.tar.gz",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public @ResponseBody byte[] getTerraformPlanBinary(@PathVariable("planId") String planId) {
        return remoteTfeService.getContentFile(planId);
    }


}
