package org.azbuilder.registry.service.provider;

import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.client.RestClient;
import org.azbuilder.api.client.model.organization.Organization;
import org.azbuilder.api.client.model.organization.provider.version.Version;
import org.azbuilder.api.client.model.organization.provider.version.file.File;
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
        ResponseWithInclude<List<Version>, File> versionsWithFile = restClient.getAllVersionsByProviderWithFile(listOrganization.getData().get(0).getId(), listOrganization.getData().get(0).getRelationships().getProvider().getData().get(0).getId());
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

    @Override
    public FileDTO getFileInformation(String organization, String provider, String version, String os, String arch) {
        Response<List<Organization>> listOrganization = restClient.getOrganizationsByNameAndProvider(organization, provider);
        String organizationId = listOrganization.getData().get(0).getId();
        String providerId = listOrganization.getData().get(0).getRelationships().getProvider().getData().get(0).getId();
        Response<List<Version>> listVersion =  restClient.getVersionsByOrganizationIdAndProviderIdAndVersionNumber(organizationId, providerId , version);
        String versionId = listVersion.getData().get(0).getId();
        Response<List<File>> files = restClient.getFileByOsArchVersion(organizationId, providerId, versionId, os, arch);

        FileDTO fileDTO = new FileDTO();
        for (File file : files.getData()) {
            fileDTO.setProtocols(List.of(listVersion.getData().get(0).getAttributes().getProtocols().split(",")));
            fileDTO.setOs(file.getAttributes().getOs());
            fileDTO.setArch(file.getAttributes().getArch());
            fileDTO.setFilename(file.getAttributes().getFilename());
            fileDTO.setDownload_url(file.getAttributes().getDownloadUrl());
            fileDTO.setShasums_url(file.getAttributes().getShasumsUrl());
            fileDTO.setShasums_signature_url(file.getAttributes().getShasumsSignatureUrl());
            fileDTO.setShasum(file.getAttributes().getShasum());

            GpgPublicKeys gpgPublicKeys = new GpgPublicKeys();
            gpgPublicKeys.setKey_id(file.getAttributes().getKeyId());
            gpgPublicKeys.setAscii_armor(file.getAttributes().getAsciiArmor());
            gpgPublicKeys.setTrust_signature(file.getAttributes().getTrustSignature());
            gpgPublicKeys.setSource(file.getAttributes().getSource());
            gpgPublicKeys.setSource_url(file.getAttributes().getSourceUrl());

            SigningKeys signingKeys = new SigningKeys();
            signingKeys.setGpg_public_keys(Arrays.asList(gpgPublicKeys));

            fileDTO.setSigning_keys(signingKeys);

        }
        return fileDTO;
    }
}
