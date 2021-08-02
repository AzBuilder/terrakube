package org.azbuilder.registry.service.provider;

import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.client.RestClient;
import org.azbuilder.api.client.model.organization.Organization;
import org.azbuilder.api.client.model.organization.provider.version.Version;
import org.azbuilder.api.client.model.organization.provider.version.implementation.Implementation;
import org.azbuilder.api.client.model.response.Response;
import org.azbuilder.api.client.model.response.ResponseWithInclude;
import org.azbuilder.registry.controller.model.provider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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
        ResponseWithInclude<List<Version>, Implementation> versionsWithFile = restClient.getAllVersionsByProviderWithImplementation(listOrganization.getData().get(0).getId(), listOrganization.getData().get(0).getRelationships().getProvider().getData().get(0).getId());
        List<VersionDTO> versionDTOList = new ArrayList<>();
        for (Version version : versionsWithFile.getData()) {
            VersionDTO versionDTO = new VersionDTO();
            versionDTO.setVersion(version.getAttributes().getVersionNumber());
            versionDTO.setProtocols(List.of(version.getAttributes().getProtocols().split(",")));
            List<PlatformDTO> platformDTOList = new ArrayList<>();
            for (Implementation implementation : versionsWithFile.getIncluded()) {
                if (implementation.getRelationships().getVersion().getData().getId().equals(version.getId())) {
                    PlatformDTO platformDTO = new PlatformDTO();
                    platformDTO.setOs(implementation.getAttributes().getOs());
                    platformDTO.setArch(implementation.getAttributes().getArch());
                    platformDTOList.add(platformDTO);
                }
            }
            versionDTO.setPlatforms(platformDTOList);
            versionDTOList.add(versionDTO);
        }
        return versionDTOList;
    }

    @Override
    public FileDTO getFileInformation(String organization, String provider, String version, String os, String arch) {
        Response<List<Organization>> listOrganization = restClient.getOrganizationsByNameAndProvider(organization, provider);
        String organizationId = listOrganization.getData().get(0).getId();
        String providerId = listOrganization.getData().get(0).getRelationships().getProvider().getData().get(0).getId();
        Response<List<Version>> listVersion =  restClient.getVersionsByOrganizationIdAndProviderIdAndVersionNumber(organizationId, providerId , version);
        String versionId = listVersion.getData().get(0).getId();
        Response<List<Implementation>> files = restClient.getImplementationByOsArchVersion(organizationId, providerId, versionId, os, arch);

        FileDTO fileDTO = new FileDTO();
        for (Implementation implementation : files.getData()) {
            fileDTO.setProtocols(List.of(listVersion.getData().get(0).getAttributes().getProtocols().split(",")));
            fileDTO.setOs(implementation.getAttributes().getOs());
            fileDTO.setArch(implementation.getAttributes().getArch());
            fileDTO.setFilename(implementation.getAttributes().getFilename());
            fileDTO.setDownload_url(implementation.getAttributes().getDownloadUrl());
            fileDTO.setShasums_url(implementation.getAttributes().getShasumsUrl());
            fileDTO.setShasums_signature_url(implementation.getAttributes().getShasumsSignatureUrl());
            fileDTO.setShasum(implementation.getAttributes().getShasum());

            GpgPublicKeys gpgPublicKeys = new GpgPublicKeys();
            gpgPublicKeys.setKey_id(implementation.getAttributes().getKeyId());
            gpgPublicKeys.setAscii_armor(implementation.getAttributes().getAsciiArmor());
            gpgPublicKeys.setTrust_signature(implementation.getAttributes().getTrustSignature());
            gpgPublicKeys.setSource(implementation.getAttributes().getSource());
            gpgPublicKeys.setSource_url(implementation.getAttributes().getSourceUrl());

            SigningKeys signingKeys = new SigningKeys();
            signingKeys.setGpg_public_keys(Arrays.asList(gpgPublicKeys));

            fileDTO.setSigning_keys(signingKeys);

        }

        return fileDTO;
    }
}
