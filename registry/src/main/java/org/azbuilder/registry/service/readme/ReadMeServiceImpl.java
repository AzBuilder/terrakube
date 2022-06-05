package org.azbuilder.registry.service.readme;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.zeroturnaround.zip.ZipUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@AllArgsConstructor
@Slf4j
@Service
public class ReadMeServiceImpl {

    private static final String README_DIRECTORY = "/.terraform-spring-boot/readme/";

    public String getContent(InputStream moduleInputStream) {
        log.info("Get content README.md");
        String readmeText = new String(Base64.getEncoder().encode("NO README FILE".getBytes(StandardCharsets.UTF_8)));
        String userHomeDirectory = FileUtils.getUserDirectoryPath();
        String gitModulePath = userHomeDirectory.concat(
                FilenameUtils.separatorsToSystem(
                        README_DIRECTORY + "/" + UUID.randomUUID()
                ));
        File gitModuleFolder = new File(gitModulePath);

        try {
            log.info("Creating temp folder");
            FileUtils.forceMkdir(gitModuleFolder);
            FileUtils.cleanDirectory(gitModuleFolder);

            File gitModuleZip = new File(gitModuleFolder.getAbsolutePath() + "/module.zip");
            log.info("Copy Input-stream to file");
            FileUtils.copyInputStreamToFile(moduleInputStream, gitModuleZip);

            ZipUtil.unpack(gitModuleZip, gitModuleFolder);

            log.info("Checking README.md");
            File readmeFile = new File(gitModuleFolder.getAbsolutePath() + "/README.md");
            readmeText = readFromInputStream(new FileInputStream(readmeFile));

            readmeText = new String(Base64.getEncoder().encode(readmeText.getBytes(StandardCharsets.UTF_8)));

            FileUtils.cleanDirectory(gitModuleFolder);
            if (gitModuleFolder.delete())
                log.info("Temp folder deleted...");

        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return readmeText;
    }

    private String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

}
