package org.terrakube.api.plugin.state;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.terrakube.api.plugin.state.model.configuration.ConfigurationData;
import org.terrakube.api.plugin.state.model.entitlement.EntitlementData;
import org.terrakube.api.plugin.state.model.organization.OrganizationData;
import org.terrakube.api.plugin.state.model.plan.PlanRunData;
import org.terrakube.api.plugin.state.model.apply.ApplyRunData;
import org.terrakube.api.plugin.state.model.runs.RunsData;
import org.terrakube.api.plugin.state.model.workspace.WorkspaceData;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
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
    public ResponseEntity<OrganizationData> getOrgInformation(@PathVariable("organizationName") String organizationName) {
        return ResponseEntity.of(Optional.ofNullable(remoteTfeService.getOrgInformation(organizationName)));
    }

    @GetMapping (produces = "application/vnd.api+json", path = "organizations/{organizationName}/workspaces/{workspaceName}")
    public ResponseEntity<WorkspaceData> getWorkspace(@PathVariable("organizationName") String organizationName, @PathVariable("workspaceName") String workspaceName) {
        log.info("Searching: {} {}", organizationName, workspaceName);
        return ResponseEntity.of(Optional.ofNullable(remoteTfeService.getWorkspace(organizationName, workspaceName, new HashMap<>())));
    }

    
    @PostMapping(produces = "application/vnd.api+json", path = "organizations/{organizationName}/workspaces")
    public ResponseEntity<WorkspaceData> createWorkspace(@PathVariable("organizationName") String organizationName, @RequestBody WorkspaceData workspaceData) {
        log.info("Create {}", workspaceData.toString());
        Optional<WorkspaceData> newWorkspace = Optional.ofNullable(remoteTfeService.createWorkspace(organizationName, workspaceData));
        if(newWorkspace.isPresent()){
            log.info("Created: {}", newWorkspace.get().toString());
            return ResponseEntity.status(201).body(newWorkspace.get());
        }else{
            return ResponseEntity.status(500).body(new WorkspaceData());
        }
    }

    @Transactional
    @PostMapping(produces = "application/vnd.api+json", path = "/workspaces/{workspaceId}/actions/lock")
    public ResponseEntity<WorkspaceData> lockWorkspace(@PathVariable("workspaceId") String workspaceId) {
        log.info("Lock {}", workspaceId);
        return ResponseEntity.of(Optional.ofNullable(remoteTfeService.updateWorkspaceLock(workspaceId, true)));
    }

    @Transactional
    @PostMapping(produces = "application/vnd.api+json", path = "/workspaces/{workspaceId}/actions/unlock")
    public ResponseEntity<WorkspaceData> unlockWorkspace(@PathVariable("workspaceId") String workspaceId) {
        log.info("Unlock {}", workspaceId);
        return ResponseEntity.of(Optional.ofNullable(remoteTfeService.updateWorkspaceLock(workspaceId, false)));
    }

    @Transactional
    @PostMapping(produces = "application/vnd.api+json", path = "/workspaces/{workspaceId}/state-versions")
    public ResponseEntity<String> createWorkspaceState(@PathVariable("workspaceId") String workspaceId, @RequestBody String StateData) {
        log.info("Create State /remote/tfe/v2/ {}", workspaceId);
        log.info("Body: {}", StateData);

        return ResponseEntity.ok("{\n" +
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
    @PutMapping (path = "/configuration-versions/{configurationid}")
    public ResponseEntity<String> uploadConfiguration(HttpServletRequest httpServletRequest, @PathVariable("configurationid") String configurationId) throws IOException {
        log.info("Uploading Id {} file", configurationId );
        remoteTfeService.uploadFile(configurationId, httpServletRequest.getInputStream());
        log.info("File created");
        return ResponseEntity.ok().body("");
    }

    @Transactional
    @PostMapping (produces = "application/vnd.api+json", path = "/runs")
    public ResponseEntity<RunsData> createRun(@RequestBody RunsData runsData) throws SchedulerException, ParseException {
        log.info("Create new run");
        return ResponseEntity.status(201).body(remoteTfeService.createRun(runsData));
    }

    @Transactional
    @GetMapping (produces = "application/vnd.api+json", path = "/runs/{runId}")
    public ResponseEntity<RunsData> getRun(@PathVariable("runId") int runId) {
        return ResponseEntity.ok(remoteTfeService.getRun(runId));
    }

    @Transactional
    @PostMapping (produces = "application/vnd.api+json", path = "/runs/{runId}/actions/apply")
    public ResponseEntity<RunsData> runApply(@PathVariable("runId") int runId) {
        return ResponseEntity.ok(remoteTfeService.runApply(runId));
    }

    @Transactional
    @PostMapping (produces = "application/vnd.api+json", path = "/runs/{runId}/actions/discard")
    public ResponseEntity<RunsData> runDiscard(@PathVariable("runId") int runId) {
        return ResponseEntity.ok(remoteTfeService.runDiscard(runId));
    }


    @Transactional
    @GetMapping (produces = "application/vnd.api+json", path = "/plans/{planId}")
    public ResponseEntity<PlanRunData> getPlan(@PathVariable("planId") int planId) {
        return ResponseEntity.ok(remoteTfeService.getPlan(planId));
    }

    @Transactional
    @GetMapping (produces = "application/vnd.api+json", path = "/applies/{applyId}")
    public ResponseEntity<ApplyRunData> getApply(@PathVariable("applyId") int applyId) {
        return ResponseEntity.ok(remoteTfeService.getApply(applyId));
    }

    @Transactional
    @GetMapping (produces = "application/vnd.api+json", path = "/plans/{planId}/logs")
    public ResponseEntity<String> getPlanLogs(@PathVariable("planId") int planId) throws IOException {
        return ResponseEntity.of(Optional.ofNullable(new String(remoteTfeService.getPlanLogs(planId), StandardCharsets.UTF_8)));
    }

    @Transactional
    @GetMapping (produces = "application/vnd.api+json", path = "/applies/{applyId}/logs")
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
