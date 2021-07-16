package org.azbuilder.registry.service.search;

import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.client.RestClient;
import org.azbuilder.api.client.model.organization.module.definition.Definition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService{

    @Autowired
    RestClient restClient;

    @Override
    public List<String> getAvailableVersions(String organizationName, String moduleName, String providerName) {
        String organizationId = restClient.getOrganizationByName(organizationName).getData().get(0).getId();

        log.info("Search Organization: {} {}", organizationName, organizationId);
        List<Definition> definitionList = restClient.getModuleByNameAndProviderWithModuleDefinition(organizationId,moduleName,providerName).getIncluded();
        log.info("Search Module: {} {}", moduleName, providerName);
        List<String> definitionVersions = new ArrayList<>();

        for (Definition definition : definitionList) {
            log.info("Definition: {} {}", definition.getId(), definition.getAttributes().get("version"));
            definitionVersions.add(definition.getAttributes().get("version"));
        }
        return definitionVersions;
    }

    @Override
    public String getModuleVersionPath(String organizationName, String moduleName, String providerName, String version) {
        String moduleVersionPath = "";

        String organizationId = restClient.getOrganizationByName(organizationName).getData().get(0).getId();
        List<Definition> definitionList = restClient.getModuleByNameAndProviderWithModuleDefinition(organizationId,moduleName,providerName).getIncluded();

        for (Definition definition : definitionList) {
            if(definition.getAttributes().get("version").equals(version))
                moduleVersionPath = definition.getAttributes().get("registryPath");
        }
        log.info("Registry Path: {}", moduleVersionPath);
        return moduleVersionPath;
    }
}
