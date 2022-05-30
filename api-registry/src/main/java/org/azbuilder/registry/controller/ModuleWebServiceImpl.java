package org.azbuilder.registry.controller;

import org.azbuilder.registry.controller.model.module.ModuleDTO;
import org.azbuilder.registry.controller.model.module.VersionDTO;
import org.azbuilder.registry.controller.model.module.VersionsDTO;
import org.azbuilder.registry.plugin.storage.StorageService;
import org.azbuilder.registry.service.module.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/terraform/modules/v1")
public class ModuleWebServiceImpl {

    @Autowired
    ModuleService moduleService;

    @Autowired
    StorageService storageService;

    @GetMapping(value = "/{organization}/{module}/{provider}/versions", produces = "application/json")
    public ResponseEntity<ModuleDTO> searchModuleVersions(@PathVariable String organization, @PathVariable String module, @PathVariable String provider) {
        VersionsDTO versionsDTO = new VersionsDTO();
        List<VersionDTO> versionDTOList = new ArrayList<>();
        for (String availableVersion : moduleService.getAvailableVersions(organization, module, provider)) {
            VersionDTO version = new VersionDTO();
            version.setVersion(availableVersion);

            versionDTOList.add(version);
        }
        versionsDTO.setVersions(versionDTOList);
        ModuleDTO moduleDTO = new ModuleDTO();
        moduleDTO.setModules(Arrays.asList(versionsDTO));
        return ResponseEntity.ok(moduleDTO);
    }

    @GetMapping(value = "/{organization}/{module}/{provider}/{version}/download", produces = "application/json")
    public ResponseEntity<ModuleDTO> getModuleVersionPath(@PathVariable String organization, @PathVariable String module, @PathVariable String provider, @PathVariable String version) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(
                "X-Terraform-Get",
                moduleService.getModuleVersionPath(organization, module, provider, version, true)
        );
        responseHeaders.set(
                "Access-Control-Expose-Headers","X-Terraform-Get"
        );
        return ResponseEntity.ok().headers(responseHeaders).body(null);
    }

    @GetMapping(
            value = "/download/{organizationName}/{moduleName}/{providerName}/{version}/module.zip",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody byte[] getModuleZip(@PathVariable String organizationName, @PathVariable String moduleName, @PathVariable String providerName, @PathVariable String version) {
        return storageService.downloadModule(organizationName, moduleName, providerName, version);
    }
}
