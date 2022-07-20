package org.terrakube.executor.plugin.tfoutput.local;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.terrakube.executor.plugin.tfoutput.TerraformOutput;
import org.terrakube.executor.plugin.tfoutput.TerraformOutputPathService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Builder
@Getter
@Setter
@Slf4j
public class LocalTerraformOutputImpl implements TerraformOutput {

    private static final String LOCAL_OUTPUT_DIRECTORY = "/.terraform-spring-boot/local/output/%s/%s/%s.tfoutput";

    @NonNull
    TerraformOutputPathService terraformOutputPathService;

    @Override
    public String save(String organizationId, String jobId, String stepId, String output, String outputError) {
        String outputFilePath = String.format(LOCAL_OUTPUT_DIRECTORY, organizationId, jobId , stepId);
        log.info("blobName: {}", outputFilePath);

        File localOutputDirectory = new File(FileUtils.getUserDirectoryPath().concat(
                FilenameUtils.separatorsToSystem(
                        outputFilePath
                )));

        log.info("Creating Output File: {}", localOutputDirectory.getAbsolutePath());
        try {
            FileUtils.writeStringToFile(localOutputDirectory, output + outputError, Charset.defaultCharset());
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return terraformOutputPathService.getOutputPath(organizationId, jobId, stepId);

    }
}
