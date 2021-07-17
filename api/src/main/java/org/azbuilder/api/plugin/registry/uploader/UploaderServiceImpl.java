package org.azbuilder.api.plugin.registry.uploader;

import org.azbuilder.api.plugin.git.GitService;
import org.azbuilder.api.plugin.registry.RegistryStorage;
import org.azbuilder.api.rs.module.Definition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class UploaderServiceImpl implements UploaderService {

    @Autowired
    RegistryStorage registryStorage;

    @Autowired
    GitService gitService;

    @Override
    public String saveDefinition(Definition definition) {
        String provider = definition.getModule().getProvider();
        String module = definition.getModule().getName();
        String organization = definition.getModule().getOrganization().getName();

        return organization + "/" + module + "/" + provider;
    }

    @Async
    private void uploadToRegistry(Definition definition){
        gitService.getCloneRepositoryByTag(definition.getModule().getSource(),definition.getVersion());
    }
}
