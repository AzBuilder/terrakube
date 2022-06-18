package org.terrakube.executor.plugin.tfoutput.aws;

import com.amazonaws.services.s3.AmazonS3;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.terrakube.executor.plugin.tfoutput.TerraformOutput;
import org.terrakube.executor.plugin.tfoutput.TerraformOutputPathService;

@Builder
@Getter
@Setter
@Slf4j
public class AwsTerraformOutputImpl implements TerraformOutput {

    @NonNull
    private AmazonS3 s3client;

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

        s3client.putObject(
                bucketName,
                blobKey,
                utf8EncodedString
        );

        return terraformOutputPathService.getOutputPath(organizationId, jobId, stepId);
    }
}
