package org.terrakube.api.plugin.storage.controller;

import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.http.ResponseEntity;
import org.terrakube.api.plugin.storage.StorageTypeService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.terrakube.api.repository.HistoryRepository;
import org.terrakube.api.rs.workspace.history.History;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/tfstate/v1")
public class TerraformStateController {

    private StorageTypeService storageTypeService;

    @GetMapping(
            value = "/organization/{organizationId}/workspace/{workspaceId}/jobId/{jobId}/step/{stepId}/terraform.tfstate",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public @ResponseBody byte[] getTerraformPlanBinary(@PathVariable("organizationId") String organizationId, @PathVariable("workspaceId") String workspaceId, @PathVariable("jobId") String jobId, @PathVariable("stepId") String stepId) {
        return storageTypeService.getTerraformPlan(organizationId, workspaceId, jobId, stepId);
    }

    @GetMapping(
            value = "/organization/{organizationId}/workspace/{workspaceId}/state/{stateFilename}.json",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody byte[] getTerraformStateJson(@PathVariable("organizationId") String organizationId, @PathVariable("workspaceId") String workspaceId, @PathVariable("stateFilename") String stateFilename) {
        return storageTypeService.getTerraformStateJson(organizationId, workspaceId, stateFilename);
    }

    @GetMapping(
            value = "/organization/{organizationId}/workspace/{workspaceId}/state/terraform.tfstate",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody byte[] getCurrentTerraformState(@PathVariable("organizationId") String organizationId, @PathVariable("workspaceId") String workspaceId) {
        return storageTypeService.getCurrentTerraformState(organizationId, workspaceId);
    }

    @PutMapping(
            value = "/organization/{organizationId}/workspace/{workspaceId}/state/terraform.tfstate",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> uploadHostedState(HttpServletRequest httpServletRequest, @PathVariable("organizationId") String organizationId, @PathVariable("workspaceId") String workspaceId) throws IOException {
        String terraformState = IOUtils.toString(httpServletRequest.getInputStream(), StandardCharsets.UTF_8.name());
        storageTypeService.uploadState(organizationId, workspaceId, terraformState);
        return ResponseEntity.status(201).body("");
    }

    @PutMapping(
            value = "/organization/{organizationId}/workspace/{workspaceId}/state/{historyId}.json",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String>  uploadJsonHostedState(HttpServletRequest httpServletRequest, @PathVariable("organizationId") String organizationId, @PathVariable("workspaceId") String workspaceId, @PathVariable("historyId") String historyId) throws IOException {
        String terraformJsonState = IOUtils.toString(httpServletRequest.getInputStream(), StandardCharsets.UTF_8.name());
        storageTypeService.uploadTerraformStateJson(organizationId, workspaceId, terraformJsonState, historyId);
        return ResponseEntity.status(201).body("");
    }
}
