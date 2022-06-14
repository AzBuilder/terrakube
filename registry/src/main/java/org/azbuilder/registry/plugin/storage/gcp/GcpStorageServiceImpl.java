package org.azbuilder.registry.plugin.storage.gcp;

import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.google.cloud.storage.BlobInfo;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.azbuilder.registry.plugin.storage.StorageService;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import org.azbuilder.registry.service.git.GitService;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Builder
public class GcpStorageServiceImpl implements StorageService {

    private static String GCP_ZIP_MODULE_LOCATION = "registry/%s/%s/%s/%s/module.zip";
    private static String GCP_DOWNLOAD_MODULE_LOCATION = "%s/terraform/modules/v1/download/%s/%s/%s/%s/module.zip";
    private static final String GCP_ERROR_LOG = "S3 Not found: {}";

    @NonNull
    String registryHostname;
    @NonNull
    private String bucketName;
    @NonNull
    private Storage storage;

    @NonNull
    GitService gitService;

    @Override
    public String searchModule(String organizationName, String moduleName, String providerName, String moduleVersion, String source, String vcsType, String accessToken) {
        String blobKey = String.format(GCP_ZIP_MODULE_LOCATION, organizationName, moduleName, providerName, moduleVersion);
        BlobId blobId = BlobId.of(
                bucketName,
                String.format(GCP_ZIP_MODULE_LOCATION, organizationName, moduleName, providerName, moduleVersion)
        );
        log.info("Checking GCP Object exist {}", blobKey);

        if (!storage.get(blobId).exists()) {
            try {
                File gitCloneDirectory = gitService.getCloneRepositoryByTag(source, moduleVersion, vcsType, accessToken);
                File moduleZip = new File(gitCloneDirectory.getAbsolutePath() + ".zip");
                ZipUtil.pack(gitCloneDirectory, moduleZip);

                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
                storage.create(blobInfo, new FileInputStream(moduleZip).readAllBytes());
                log.info("File uploaded to bucket {} as {}", bucketName, blobKey);

                FileUtils.cleanDirectory(gitCloneDirectory);
                if (moduleZip.delete()) log.info("Successfully delete folder");
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        return String.format(GCP_DOWNLOAD_MODULE_LOCATION, registryHostname, organizationName, moduleName, providerName, moduleVersion);
    }

    @Override
    public byte[] downloadModule(String organizationName, String moduleName, String providerName, String moduleVersion) {
        byte[] data;
        log.info("Searching: /registry/{}/{}/{}/{}/module.zip", organizationName, moduleName, providerName, moduleVersion);
        data = storage.get(
                        BlobId.of(
                                bucketName,
                                String.format(GCP_ZIP_MODULE_LOCATION, organizationName, moduleName, providerName, moduleVersion)))
                .getContent();
        return data;
    }
}
