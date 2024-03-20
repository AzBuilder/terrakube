package org.terrakube.registry.plugin.storage.gcp;

import com.google.cloud.storage.BlobInfo;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.terrakube.registry.plugin.storage.StorageService;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import org.terrakube.registry.service.git.GitService;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;

@Slf4j
@Builder
public class GcpStorageServiceImpl implements StorageService {

    private static String gcpZipModuleLocation = "registry/%s/%s/%s/%s/module.zip";
    private static String gcpDownloadModuleLocation = "%s/terraform/modules/v1/download/%s/%s/%s/%s/module.zip";

    @NonNull
    private String registryHostname;
    @NonNull
    private String bucketName;
    @NonNull
    private Storage storage;

    @NonNull
    private GitService gitService;

    @Override
    public String searchModule(String organizationName, String moduleName, String providerName, String moduleVersion, String source, String vcsType, String accessToken, String tagPrefix, String folder) {
        String blobKey = String.format(gcpZipModuleLocation, organizationName, moduleName, providerName, moduleVersion);
        log.info("Searching module: {}", blobKey);
        BlobId blobId = BlobId.of(
                bucketName,
                String.format(gcpZipModuleLocation, organizationName, moduleName, providerName, moduleVersion)
        );
        log.info("Checking GCP Object exist {}", blobKey);
        if (storage.get(blobId) == null) {
            try {
                File gitCloneDirectory = gitService.getCloneRepositoryByTag(source, moduleVersion, vcsType, accessToken, tagPrefix, folder);
                File moduleZip = new File(gitCloneDirectory.getAbsolutePath() + ".zip");
                ZipUtil.pack(gitCloneDirectory, moduleZip);

                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
                storage.create(blobInfo, FileUtils.readFileToByteArray(moduleZip));

                log.info("File uploaded to bucket {} as {}", bucketName, blobKey);

                FileUtils.cleanDirectory(gitCloneDirectory);
                if (FileUtils.deleteQuietly(moduleZip)) log.info("Successfully delete folder for gcp module");
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        return String.format(gcpDownloadModuleLocation, registryHostname, organizationName, moduleName, providerName, moduleVersion);
    }

    @Override
    public byte[] downloadModule(String organizationName, String moduleName, String providerName, String moduleVersion) {
        byte[] data;
        log.info("Searching: /registry/{}/{}/{}/{}/module.zip", organizationName, moduleName, providerName, moduleVersion);
        data = storage.get(
                        BlobId.of(
                                bucketName,
                                String.format(gcpZipModuleLocation, organizationName, moduleName, providerName, moduleVersion)))
                .getContent();
        return data;
    }
}
