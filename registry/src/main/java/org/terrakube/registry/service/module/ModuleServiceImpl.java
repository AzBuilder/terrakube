package org.terrakube.registry.service.module;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.terrakube.client.TerrakubeClient;
import org.terrakube.client.model.organization.module.Module;
import org.terrakube.client.model.organization.module.ModuleAttributes;
import org.terrakube.client.model.organization.module.ModuleRequest;
import org.terrakube.client.model.organization.ssh.Ssh;
import org.terrakube.client.model.organization.vcs.Vcs;
import org.terrakube.registry.plugin.storage.StorageService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Slf4j
@Service
public class ModuleServiceImpl implements ModuleService {

    TerrakubeClient terrakubeClient;
    StorageService storageService;

    @Override
    public List<String> getAvailableVersions(String organizationName, String moduleName, String providerName) {
        String organizationId = terrakubeClient.getOrganizationByName(organizationName).getData().get(0).getId();

        log.info("Search Organization: {} {}", organizationName, organizationId);
        List<String> versionList = terrakubeClient.getModuleByNameAndProvider(organizationId, moduleName, providerName).getData().get(0).getAttributes().getVersions();
        log.info("Search Module: {} {}", moduleName, providerName);
        List<String> definitionVersions = new ArrayList<>();

        for (String version : versionList) {
            log.info("Version: {}", version);
            definitionVersions.add(version);
        }
        return definitionVersions;
    }

    @Override
    public String getModuleVersionPath(String organizationName, String moduleName, String providerName, String version, boolean countDownload) {
        String moduleVersionPath = "";

        String organizationId = terrakubeClient.getOrganizationByName(organizationName).getData().get(0).getId();
        Module module = terrakubeClient.getModuleByNameAndProvider(organizationId, moduleName, providerName).getData().get(0);
        String moduleSource = module.getAttributes().getSource();
        String vcsType = "PUBLIC";
        String accessToken = null;
        if (module.getRelationships().getVcs().getData() != null) {
            Vcs vcsInformation = getVcsInformation(organizationId, module.getRelationships().getVcs().getData().getId());
            vcsType = vcsInformation.getAttributes().getVcsType();
            accessToken = vcsInformation.getAttributes().getAccessToken();
        }

        if (module.getRelationships().getSsh().getData() != null) {
            Ssh sshInformation = getSshInformation(organizationId, module.getRelationships().getSsh().getData().getId());
            vcsType = "SSH~" + sshInformation.getAttributes().getSshType();
            accessToken = sshInformation.getAttributes().getPrivateKey();
        }

        moduleVersionPath = storageService.searchModule(
                organizationName, moduleName, providerName, version, moduleSource, vcsType, accessToken
        );

        if (countDownload)
            updateModuleDownloadCount(organizationId, module);

        log.info("Registry Path: {}", moduleVersionPath);
        return moduleVersionPath;
    }

    private void updateModuleDownloadCount(String organizationId, Module module) {
        log.info("Update module download count");
        ModuleRequest moduleRequest = new ModuleRequest();
        ModuleAttributes moduleAttributes = new ModuleAttributes();
        moduleAttributes.setDownloadQuantity(module.getAttributes().getDownloadQuantity() + 1);
        module.setAttributes(moduleAttributes);
        moduleRequest.setData(module);

        terrakubeClient.updateModule(moduleRequest, organizationId, module.getId());
    }

    private Vcs getVcsInformation(String organizationId, String vcsId) {
        return terrakubeClient.getVcsById(organizationId, vcsId).getData();
    }

    private Ssh getSshInformation(String organizationId, String sshId) {
        return terrakubeClient.getSshById(organizationId, sshId).getData();
    }
}
