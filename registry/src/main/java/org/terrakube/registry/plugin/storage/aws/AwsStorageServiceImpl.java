package org.terrakube.registry.plugin.storage.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.terrakube.registry.plugin.storage.StorageService;
import org.terrakube.registry.service.git.GitService;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;

@Slf4j
@Builder
public class AwsStorageServiceImpl implements StorageService {

    private static String BUCKET_ZIP_MODULE_LOCATION = "registry/%s/%s/%s/%s/module.zip";
    private static String BUCKET_DOWNLOAD_MODULE_LOCATION = "%s/terraform/modules/v1/download/%s/%s/%s/%s/module.zip";
    private static final String S3_ERROR_LOG = "S3 Not found: {}";

    @NonNull
    private AmazonS3 s3client;

    @NonNull
    private String bucketName;

    @NonNull
    GitService gitService;

    @NonNull
    String registryHostname;

    @Override
    public String searchModule(String organizationName, String moduleName, String providerName, String moduleVersion,
            String source, String vcsType, String vcsConnectionType, String accessToken, String tagPrefix,
            String folder) {
        String blobKey = String.format(BUCKET_ZIP_MODULE_LOCATION, organizationName, moduleName, providerName,
                moduleVersion);
        log.info("Checking Aws S3 Object exist {}", blobKey);

        if (!s3client.doesObjectExist(bucketName, blobKey)) {
            File gitCloneDirectory = gitService.getCloneRepositoryByTag(source, moduleVersion, vcsType,
                    vcsConnectionType, accessToken, tagPrefix, folder);
            File moduleZip = new File(gitCloneDirectory.getAbsolutePath() + ".zip");
            ZipUtil.pack(gitCloneDirectory, moduleZip);

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, blobKey, moduleZip);

            s3client.putObject(putObjectRequest);

            log.info("Upload Aws S3 Object completed", blobKey);
            try {
                FileUtils.cleanDirectory(gitCloneDirectory);
                if (FileUtils.deleteQuietly(moduleZip))
                    log.info("Successfully delete folder");
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        return String.format(BUCKET_DOWNLOAD_MODULE_LOCATION, registryHostname, organizationName, moduleName,
                providerName, moduleVersion);
    }

    @Override
    public byte[] downloadModule(String organizationName, String moduleName, String providerName,
            String moduleVersion) {
        byte[] data;
        try {
            log.info("Searching: /registry/{}/{}/{}/{}/module.zip", organizationName, moduleName, providerName,
                    moduleVersion);
            S3Object s3object = s3client.getObject(bucketName, String.format(BUCKET_ZIP_MODULE_LOCATION,
                    organizationName, moduleName, providerName, moduleVersion));
            S3ObjectInputStream inputStream = s3object.getObjectContent();
            data = inputStream.getDelegateStream().readAllBytes();
        } catch (IOException e) {
            log.error(S3_ERROR_LOG, e.getMessage());
            data = new byte[0];
        }
        return data;
    }

}
