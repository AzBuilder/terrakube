package org.azbuilder.executor.plugin.tfoutput.azure;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.azbuilder.executor.plugin.tfoutput.TerraformOutput;
import org.azbuilder.executor.plugin.tfoutput.TerraformOutputPathService;

@Slf4j
@Builder
public class AzureTerraformOutputImpl implements TerraformOutput {

    private static final String CONTAINER_NAME = "tfoutput";

    @NonNull
    BlobServiceClient blobServiceClient;

    @NonNull
    TerraformOutputPathService terraformOutputPathService;

    @Override
    public String save(String organizationId, String jobId, String stepId, String output, String outputError) {
        BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME);

        log.info("blobContainerClient.exists {}", blobContainerClient.exists());
        if (!blobContainerClient.exists()) {
            blobContainerClient.create();
        }
        String blobName = organizationId + "/" + jobId + "/" +stepId + ".tfoutput";
        log.info("blobName: {}", blobName);
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);

        byte[] bytes = StringUtils.getBytesUtf8(output + outputError);
        String utf8EncodedString = StringUtils.newStringUtf8(bytes);
        blobClient.upload(BinaryData.fromString(utf8EncodedString));

        return terraformOutputPathService.getOutputPath(organizationId, jobId, stepId);
    }
}
