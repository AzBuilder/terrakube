package org.terrakube.registry.plugin.storage.aws;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.terrakube.registry.plugin.storage.StorageService;
import org.terrakube.registry.service.git.GitService;
import org.zeroturnaround.zip.ZipUtil;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;

@Slf4j
@Builder
public class AwsStorageServiceImpl implements StorageService {

    private static String BUCKET_ZIP_MODULE_LOCATION = "registry/%s/%s/%s/%s/module.zip";
    private static String BUCKET_DOWNLOAD_MODULE_LOCATION = "%s/terraform/modules/v1/download/%s/%s/%s/%s/module.zip";
    private static final String S3_ERROR_LOG = "S3 Not found: {}";

    @NonNull
    private S3Client s3client;

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

        if (!doesObjectExistByListObjects(bucketName, blobKey)) {
            File gitCloneDirectory = gitService.getCloneRepositoryByTag(source, moduleVersion, vcsType,
                    vcsConnectionType, accessToken, tagPrefix, folder);
            File moduleZip = new File(gitCloneDirectory.getAbsolutePath() + ".zip");
            ZipUtil.pack(gitCloneDirectory, moduleZip);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(blobKey)
                    .build();

            s3client.putObject(putObjectRequest, RequestBody.fromFile(moduleZip));

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

    public boolean doesObjectExistByListObjects(String bucketName, String key) {
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();
        ListObjectsV2Response listObjectsV2Response = s3client.listObjectsV2(listObjectsV2Request);

        return listObjectsV2Response.contents()
                .stream()
                .filter(s3ObjectSummary -> s3ObjectSummary.getValueForField("key", String.class)
                        .equals(key))
                .findFirst()
                .isPresent();
    }

    @Override
    public byte[] downloadModule(String organizationName, String moduleName, String providerName,
            String moduleVersion) {
        byte[] data;
        try {
            log.info("Searching: /registry/{}/{}/{}/{}/module.zip", organizationName, moduleName, providerName,
                    moduleVersion);
            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .key(String.format(BUCKET_ZIP_MODULE_LOCATION,
                            organizationName, moduleName, providerName, moduleVersion))
                    .bucket(bucketName)
                    .build();
            ResponseBytes<GetObjectResponse> objectBytes = s3client.getObject(objectRequest,
                    ResponseTransformer.toBytes());
            data = objectBytes.asByteArray();
        } catch (Exception e) {
            log.error(S3_ERROR_LOG, e.getMessage());
            data = new byte[0];
        }
        return data;
    }

}
