package org.azbuilder.registry.controller;

import lombok.extern.slf4j.Slf4j;
import org.azbuilder.registry.controller.model.module.ModuleDTO;
import org.azbuilder.registry.controller.model.provider.VersionsDTO;
import org.azbuilder.registry.controller.model.provider.VersionDTO;
import org.azbuilder.registry.service.provider.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/terraform/providers/v1")
public class ProviderWebServiceImpl {

    @Autowired
    ProviderService providerService;

    @GetMapping(value = "/{organization}/{provider}/versions", produces = "application/json")
    public ResponseEntity<VersionsDTO> searchModuleVersions(@PathVariable String organization, @PathVariable String provider) {
        List<VersionDTO> versionDTOList = providerService.getAvailableVersions(organization, provider);
        VersionsDTO versionsDTO = new VersionsDTO();
        versionsDTO.setVersions(versionDTOList);
        return ResponseEntity.ok(versionsDTO);
    }

    @GetMapping(value = "/{organization}/{provider}/{version}/download/{os}/{arch}", produces = "application/json")
    public ResponseEntity<ModuleDTO> getModuleVersionPath(@PathVariable String organization, @PathVariable String module, @PathVariable String provider, @PathVariable String version) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(
                "X-Terraform-Get",
                ""
        );
        return ResponseEntity.ok().headers(responseHeaders).body(null);
    }
}
