package org.azbuilder.executor.plugin.tfoutput.gcp;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.azbuilder.executor.plugin.tfoutput.TerraformOutput;
import org.azbuilder.executor.plugin.tfoutput.TerraformOutputPathService;

@Slf4j
@Builder
public class GcpTerraformOutputImpl implements TerraformOutput {

    @NonNull
    private Storage storage;

    @NonNull
    private String bucketName;

    @NonNull
    TerraformOutputPathService terraformOutputPathService;
    @Override
    public String save(String organizationId, String jobId, String stepId, String output, String outputError) {
        String blobKey = String.format("tfoutput/%s/%s/%s.tfoutput",organizationId, jobId, stepId);
        log.info("blobKey: {}", blobKey);

        byte[] bytes = StringUtils.getBytesUtf8(output + outputError);
        String utf8EncodedString = StringUtils.newStringUtf8(bytes);
        BlobId blobId = BlobId.of(bucketName, blobKey);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, utf8EncodedString.getBytes());
        log.info("File uploaded to bucket {} as {}", bucketName, blobKey);

        return terraformOutputPathService.getOutputPath(organizationId, jobId, stepId);
    }
}
