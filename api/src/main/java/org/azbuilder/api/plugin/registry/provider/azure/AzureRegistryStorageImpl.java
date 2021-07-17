package org.azbuilder.api.plugin.registry.provider.azure;

import org.azbuilder.api.plugin.registry.RegistryStorage;
import org.azbuilder.api.rs.module.Definition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AzureRegistryStorageImpl implements RegistryStorage {

    @Autowired
    AzureRegistryStorageProperties azureRegistryStorageProperties;

    @Override
    public String modulePath(Definition definition) {
        return null;
    }
}
