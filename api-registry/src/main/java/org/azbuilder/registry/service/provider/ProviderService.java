package org.azbuilder.registry.service.provider;

import org.azbuilder.registry.controller.model.provider.VersionDTO;

import java.util.List;

public interface ProviderService {
    List<VersionDTO> getAvailableVersions(String organization, String provider);
}
