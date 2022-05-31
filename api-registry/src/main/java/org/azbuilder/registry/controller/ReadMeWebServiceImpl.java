package org.azbuilder.registry.controller;

import lombok.AllArgsConstructor;
import org.azbuilder.registry.controller.model.ReadMe;
import org.azbuilder.registry.plugin.storage.StorageService;
import org.azbuilder.registry.service.module.ModuleService;
import org.azbuilder.registry.service.readme.ReadMeServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;


@AllArgsConstructor
@RestController
@RequestMapping("/terraform/readme/v1")
public class ReadMeWebServiceImpl {

    ModuleService moduleService;
    ReadMeServiceImpl readMeService;
    StorageService storageService;

    @GetMapping(value = "/{organization}/{module}/{provider}/{version}/download", produces = "application/json")
    public ResponseEntity<ReadMe> getModuleVersionPath(@PathVariable String organization, @PathVariable String module, @PathVariable String provider, @PathVariable String version) {
        ReadMe readMe = new ReadMe();
        String moduleURL = moduleService.getModuleVersionPath(organization, module, provider, version, false);
        readMe.setUrl(moduleURL);
        readMe.setContent(readMeService.getContent(new ByteArrayInputStream(storageService.downloadModule(organization, module, provider, version))));
        return ResponseEntity.ok().body(readMe);
    }
}
