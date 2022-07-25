package org.terrakube.registry.plugin.storage.local;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.terrakube.registry.plugin.storage.StorageService;
import org.terrakube.registry.service.git.GitService;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Builder
@Slf4j
public class LocalStorageServiceImpl implements StorageService {

    private static String DOWNLOAD_MODULE_LOCATION = "%s/terraform/modules/v1/download/%s/%s/%s/%s/module.zip";
    private static String MODULE_LOCATION = "/.terraform-spring-boot/local/modules/%s/%s/%s/%s/module.zip";

    @NonNull
    GitService gitService;
    @NonNull
    String registryHostname;

    @Override
    public synchronized String searchModule(String organizationName, String moduleName, String providerName, String moduleVersion, String source, String vcsType, String accessToken) {
        String moduleFilePath = String.format(MODULE_LOCATION, organizationName, moduleName, providerName, moduleVersion);
        log.info("blobName: {}", moduleFilePath);
        File moduleFile = new File(FileUtils.getUserDirectoryPath().concat(moduleFilePath));
        if (moduleFile.exists()) {
            File gitCloneDirectory = gitService.getCloneRepositoryByTag(source, moduleVersion, vcsType, accessToken);
            File moduleZip = new File(gitCloneDirectory.getAbsolutePath() + ".zip");
            ZipUtil.pack(gitCloneDirectory, moduleZip);
        }

        return String.format(MODULE_LOCATION, registryHostname, organizationName, moduleName, providerName, moduleVersion);
    }

    @Override
    public byte[] downloadModule(String organizationName, String moduleName, String providerName, String moduleVersion) {
        String pathModule = String.format(MODULE_LOCATION, organizationName, moduleName, providerName, moduleVersion);
        File localOutputDirectory = new File(FileUtils.getUserDirectoryPath().concat(pathModule));
        try {
            return IOUtils.toByteArray(new FileInputStream(localOutputDirectory));
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
