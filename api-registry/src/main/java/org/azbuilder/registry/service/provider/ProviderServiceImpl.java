package org.azbuilder.registry.service.provider;

import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.client.RestClient;
import org.azbuilder.api.client.model.organization.Organization;
import org.azbuilder.api.client.model.organization.provider.version.Version;
import org.azbuilder.api.client.model.organization.provider.version.file.File;
import org.azbuilder.api.client.model.response.Response;
import org.azbuilder.api.client.model.response.ResponseWithInclude;
import org.azbuilder.registry.controller.model.provider.PlatformDTO;
import org.azbuilder.registry.controller.model.provider.VersionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ProviderServiceImpl implements ProviderService {

    @Autowired
    RestClient restClient;

    @Override
    public List<VersionDTO> getAvailableVersions(String organization, String provider) {
        log.info("Organization Provider: {} {}", organization, provider);
        Response<List<Organization>> listOrganization = restClient.getOrganizationsByNameAndProvider(organization, provider);
        log.info("Size Response 1: {}", listOrganization.getData().size());
        ResponseWithInclude<List<Version>, File> versionsWithFile = restClient.getAllVersionsByProviderWithFile(listOrganization.getData().get(0).getId(), listOrganization.getData().get(0).getRelationships().getProvider().getData().get(0).getId());
        log.info("Size Response 2: {}", versionsWithFile.getData().size());
        List<VersionDTO> versionDTOList = new ArrayList<>();
        for (Version version : versionsWithFile.getData()) {
            VersionDTO versionDTO = new VersionDTO();
            versionDTO.setVersion(version.getAttributes().getVersionNumber());
            versionDTO.setProtocols(List.of(version.getAttributes().getProtocols().split(",")));
            List<PlatformDTO> platformDTOList = new ArrayList<>();
            for (File file : versionsWithFile.getIncluded()) {
                if (file.getRelationships().getVersion().getData().getId().equals(version.getId())) {
                    PlatformDTO platformDTO = new PlatformDTO();
                    platformDTO.setOs(file.getAttributes().getOs());
                    platformDTO.setArch(file.getAttributes().getArch());
                    platformDTOList.add(platformDTO);
                }
            }
            versionDTO.setPlatforms(platformDTOList);
            versionDTOList.add(versionDTO);
        }

        return versionDTOList;
    }
}
