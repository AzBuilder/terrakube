package org.terrakube.executor.plugin.tfoutput.aws;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.terrakube.executor.plugin.tfoutput.TerraformOutput;
import org.terrakube.executor.plugin.tfoutput.TerraformOutputPathService;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Builder
@Getter
@Setter
@Slf4j
public class AwsTerraformOutputImpl implements TerraformOutput {

    @NonNull
    private S3Client s3client;

    @NonNull
    private String bucketName;

    @NonNull
    TerraformOutputPathService terraformOutputPathService;

    @Override
    public String save(String organizationId, String jobId, String stepId, String output, String outputError) {
        String blobKey = "tfoutput/" + organizationId + "/" + jobId + "/" + stepId + ".tfoutput";
        log.info("blobKey: {}", blobKey);

        byte[] bytes = StringUtils.getBytesUtf8(output + outputError);
        String utf8EncodedString = StringUtils.newStringUtf8(bytes);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(blobKey)
                .build();

        s3client.putObject(putObjectRequest, RequestBody.fromString(utf8EncodedString));
        log.info("Upload Object {} completed", blobKey);

        return terraformOutputPathService.getOutputPath(organizationId, jobId, stepId);
    }
}
