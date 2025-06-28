package io.terrakube.registry.service.provider;

import io.terrakube.registry.controller.model.provider.FileDTO;
import io.terrakube.registry.controller.model.provider.VersionDTO;

import java.util.List;

public interface ProviderService {
    List<VersionDTO> getAvailableVersions(String organization, String provider);

    FileDTO getFileInformation(String organization, String provider, String version, String os, String arch);
}
