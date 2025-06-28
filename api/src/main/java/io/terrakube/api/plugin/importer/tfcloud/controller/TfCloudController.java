package io.terrakube.api.plugin.importer.tfcloud.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.terrakube.api.plugin.importer.tfcloud.WorkspaceImport;
import io.terrakube.api.plugin.importer.tfcloud.WorkspaceImportRequest;
import io.terrakube.api.plugin.importer.tfcloud.services.WorkspaceService;

import java.util.List;

@RestController
@RequestMapping("/importer/tfcloud")
public class TfCloudController {

    private final WorkspaceService service;

    public TfCloudController(WorkspaceService service) {
        this.service = service;
    }

    @GetMapping("/workspaces")
    public List<WorkspaceImport.WorkspaceData> getWorkspaces(@RequestHeader("X-TFC-Url") String apiUrl,@RequestHeader("X-TFC-Token") String apiToken,
            @RequestParam String organization) {
        return service.getWorkspaces(apiToken,apiUrl, organization);
    }

    @PostMapping("/workspaces")
    public ResponseEntity<?> importWorkspaces(@RequestHeader("X-TFC-Url") String apiUrl,@RequestHeader("X-TFC-Token") String apiToken,@RequestBody WorkspaceImportRequest request) {
        String result = service.importWorkspace(apiToken,apiUrl,request);
        return ResponseEntity.ok().body(result);
    }

}
