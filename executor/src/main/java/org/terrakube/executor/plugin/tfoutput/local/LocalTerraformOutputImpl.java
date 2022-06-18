package org.terrakube.executor.plugin.tfoutput.local;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.terrakube.executor.plugin.tfoutput.TerraformOutput;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.stream.Stream;

@Slf4j
public class LocalTerraformOutputImpl implements TerraformOutput {

    private static final String LOCAL_OUTPUT_DIRECTORY = "/.terraform-spring-boot/local/output/";

    @Override
    public String save(String organizationId, String jobId, String stepId, String output, String outputError) {
        String outputFilePath = String.join(File.separator, Stream.of(organizationId, jobId, stepId + ".tfoutput").toArray(String[]::new));
        log.info("blobName: {}", outputFilePath);

        File localOutputDirectory = new File(FileUtils.getUserDirectoryPath().concat(
                FilenameUtils.separatorsToSystem(
                        LOCAL_OUTPUT_DIRECTORY + outputFilePath
                )));

        log.info("Creating Output File: {}", localOutputDirectory.getAbsolutePath());
        try {
            FileUtils.writeStringToFile(localOutputDirectory, output + outputError, Charset.defaultCharset());
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return String.format("http://localhost:8090/output/%s", outputFilePath);

    }
}
