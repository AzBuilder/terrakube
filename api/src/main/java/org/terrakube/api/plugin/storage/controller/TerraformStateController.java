package org.terrakube.api.plugin.storage.controller;

import lombok.AllArgsConstructor;
import org.terrakube.api.plugin.storage.StorageTypeService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
    public @ResponseBody byte[] getTerraformState(@PathVariable("organizationId") String organizationId, @PathVariable("workspaceId") String workspaceId, @PathVariable("stateFilename") String stateFilename) {
        return storageTypeService.getTerraformStateJson(organizationId, workspaceId, stateFilename);
    }
}
