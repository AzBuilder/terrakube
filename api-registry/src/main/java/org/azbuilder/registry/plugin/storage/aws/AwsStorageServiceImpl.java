package org.azbuilder.registry.plugin.storage.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.azbuilder.registry.plugin.storage.StorageService;
import org.azbuilder.registry.service.git.GitService;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;

@Slf4j
@Builder
public class AwsStorageServiceImpl implements StorageService {

    @NonNull
    private AmazonS3 s3client;

    @NonNull
    private String bucketName;

    @NonNull
    GitService gitService;

    @Override
    public String searchModule(String organizationName, String moduleName, String providerName, String moduleVersion, String source, String vcsType, String accessToken) {
        String blobKey = String.format("registry/%s/%s/%s/%s/module.zip", organizationName, moduleName, providerName, moduleVersion);
        log.info("Checking Aws S3 Object exist {}", blobKey);

        if (!s3client.doesObjectExist(bucketName, blobKey)) {
            File gitCloneDirectory = gitService.getCloneRepositoryByTag(source, moduleVersion, vcsType, accessToken);
            File moduleZip = new File(gitCloneDirectory.getAbsolutePath() + ".zip");
            ZipUtil.pack(gitCloneDirectory, moduleZip);

            s3client.putObject(new PutObjectRequest(bucketName, blobKey, moduleZip).withCannedAcl(CannedAccessControlList.PublicRead));

            log.info("Upload Aws S3 Object completed", blobKey);
            try {
                FileUtils.cleanDirectory(gitCloneDirectory);
                if (moduleZip.delete()) log.info("Successfully delete folder");
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        return s3client.getUrl(bucketName, blobKey).toExternalForm();
    }

}
