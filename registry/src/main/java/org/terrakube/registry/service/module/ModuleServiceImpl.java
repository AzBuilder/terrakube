package org.terrakube.registry.service.module;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.terrakube.client.TerrakubeClient;
import org.terrakube.client.model.organization.module.Module;
import org.terrakube.client.model.organization.module.ModuleAttributes;
import org.terrakube.client.model.organization.module.ModuleRequest;
import org.terrakube.client.model.organization.ssh.Ssh;
import org.terrakube.client.model.organization.vcs.Vcs;
import org.terrakube.client.model.organization.vcs.github_app_token.GitHubAppToken;
import org.terrakube.registry.plugin.storage.StorageService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
        String vcsConnectionType = null;
        String folder = module.getAttributes().getFolder();
        String tagPrefix = module.getAttributes().getTagPrefix();

        if (module.getRelationships().getVcs().getData() != null) {
            Vcs vcsInformation = getVcsInformation(organizationId, module.getRelationships().getVcs().getData().getId());
            vcsType = vcsInformation.getAttributes().getVcsType();
            vcsConnectionType = vcsInformation.getAttributes().getConnectionType();
            accessToken = getAccessToken(organizationId, vcsInformation.getId(), moduleSource);
        }

        if (module.getRelationships().getSsh().getData() != null) {
            Ssh sshInformation = getSshInformation(organizationId, module.getRelationships().getSsh().getData().getId());
            vcsType = "SSH~" + sshInformation.getAttributes().getSshType();
            accessToken = sshInformation.getAttributes().getPrivateKey();
        }

        moduleVersionPath = storageService.searchModule(
                organizationName, moduleName, providerName, version, moduleSource, vcsType, vcsConnectionType, accessToken, tagPrefix, folder
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

    private String getAccessToken(String organizationId, String vcsId, String repository_source) {
        Vcs vcs = getVcsInformation(organizationId, vcsId);
        if (vcs == null) return null;
        String token = vcs.getAttributes().getAccessToken();
        if(token == null && vcs.getAttributes().getConnectionType().equals("STANDALONE")) {
            log.info("The VCS connection is on a standalone app, getting the GitHub App token");
            GitHubAppToken gitHubAppToken = getGitHubAppTokenInformation(repository_source, vcs.getAttributes().getClientId());
            token = gitHubAppToken.getAttributes().getToken();
        }
        return token;
    }
    private Vcs getVcsInformation(String organizationId, String vcsId) {
        return terrakubeClient.getVcsById(organizationId, vcsId).getData();
    }

    private Ssh getSshInformation(String organizationId, String sshId) {
        return terrakubeClient.getSshById(organizationId, sshId).getData();
    }
    
    private GitHubAppToken getGitHubAppTokenInformation(String vcsClientId, String repository_source) {
        URI uri = URI.create(repository_source);
        String owner = uri.getPath().split("/")[1];
        List<GitHubAppToken> gitHubAppTokens = terrakubeClient.getGitHubAppTokenByVcsIdAndOwner(owner, vcsClientId).getData();
        if (gitHubAppTokens.size() == 0)  return null;
        return gitHubAppTokens.get(0);
    } 
}
