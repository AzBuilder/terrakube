package org.terrakube.api.plugin.storage.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.ResponseEntity;
import org.terrakube.api.plugin.storage.StorageTypeService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.terrakube.api.repository.ArchiveRepository;
import org.terrakube.api.repository.HistoryRepository;
import org.terrakube.api.rs.workspace.history.History;
import org.terrakube.api.rs.workspace.history.archive.Archive;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@RestController
@Slf4j
@RequestMapping("/tfstate/v1")
public class TerraformStateController {

    private StorageTypeService storageTypeService;

    private ArchiveRepository archiveRepository;

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
            value = "/archive/{archiveId}/terraform.tfstate",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> uploadHostedState(HttpServletRequest httpServletRequest, @PathVariable("archiveId") String archiveId) throws IOException {
        log.info("uploadHostedState for: {}", archiveId);
        Optional<Archive> archive = archiveRepository.findById(UUID.fromString(archiveId));
        if (archive.isPresent()) {
            Archive archiveData = archive.get();
            String terraformState = IOUtils.toString(httpServletRequest.getInputStream(), StandardCharsets.UTF_8.name());
            storageTypeService.uploadState(
                    archiveData.getHistory().getWorkspace().getOrganization().getId().toString(),
                    archiveData.getHistory().getWorkspace().getId().toString(),
                    terraformState
            );
            archiveRepository.deleteById(archiveData.getId());
            return ResponseEntity.status(201).body("");
        } else {
            return ResponseEntity.status(403).body("");
        }
    }

    @PutMapping(
            value = "/archive/{archiveId}/terraform.json.tfstate",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> uploadJsonHostedState(HttpServletRequest httpServletRequest, @PathVariable("archiveId") String archiveId) throws IOException {
        log.info("uploadJsonHostedState for: {}", archiveId);
        Optional<Archive> archive = archiveRepository.findById(UUID.fromString(archiveId));
        if (archive.isPresent()) {
            Archive archiveData = archive.get();
            String terraformJsonState = IOUtils.toString(httpServletRequest.getInputStream(), StandardCharsets.UTF_8.name());
            storageTypeService.uploadTerraformStateJson(
                    archiveData.getHistory().getWorkspace().getOrganization().getId().toString(),
                    archiveData.getHistory().getWorkspace().getId().toString(),
                    terraformJsonState,
                    archiveData.getHistory().getId().toString()
            );
            archiveRepository.deleteById(archiveData.getId());
            return ResponseEntity.status(201).body("");
        } else {
            return ResponseEntity.status(403).body("");
        }
    }
}
