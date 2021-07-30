package org.azbuilder.registry.service.module;

import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.client.RestClient;
import org.azbuilder.api.client.model.organization.module.Module;
import org.azbuilder.registry.plugin.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ModuleServiceImpl implements ModuleService {

    private static final String GIT_DIRECTORY="/.terraform-spring-boot/git/";

    @Autowired
    RestClient restClient;

    @Autowired
    StorageService storageService;

    @Override
    public List<String> getAvailableVersions(String organizationName, String moduleName, String providerName) {
        String organizationId = restClient.getOrganizationByName(organizationName).getData().get(0).getId();

        log.info("Search Organization: {} {}", organizationName, organizationId);
        List<String> versionList = restClient.getModuleByNameAndProvider(organizationId, moduleName, providerName).getData().get(0).getAttributes().getVersions();
        log.info("Search Module: {} {}", moduleName, providerName);
        List<String> definitionVersions = new ArrayList<>();

        for (String version : versionList) {
            log.info("Version: {}", version);
            definitionVersions.add(version);
        }
        return definitionVersions;
    }

    @Override
    public String getModuleVersionPath(String organizationName, String moduleName, String providerName, String version) {
        String moduleVersionPath = "";

        String organizationId = restClient.getOrganizationByName(organizationName).getData().get(0).getId();
        List<Module> moduleList = restClient.getModuleByNameAndProvider(organizationId, moduleName, providerName).getData();
        List<String> versionList = moduleList.get(0).getAttributes().getVersions();
        for (String moduleVersion : versionList) {
            if (moduleVersion.equals(version))
                moduleVersionPath = storageService.searchModule(
                        organizationName, moduleName, providerName, moduleVersion, moduleList.get(0).getAttributes().getSource()
                );
        }
        log.info("Registry Path: {}", moduleVersionPath);
        return moduleVersionPath;
    }
}
