package org.terrakube.executor.service.mode.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.terrakube.executor.configuration.ExecutorFlagsProperties;
import org.terrakube.executor.service.executor.ExecutorJob;
import org.terrakube.executor.service.mode.TerraformJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
public class BatchModeServiceImpl implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    ExecutorJob executorJob;

    @Autowired
    ExecutorFlagsProperties executorFlagsProperties;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (executorFlagsProperties.isBatch())
            try {
                File terraformJobFile = new File(executorFlagsProperties.getBatchJobFilePath());
                TerraformJob terraformJob = new ObjectMapper().readValue(terraformJobFile, TerraformJob.class);
                executorJob.createJob(terraformJob);
                if (!terraformJobFile.delete()) log.error("Error deleting folder");
            } catch (IOException exception) {
                log.error(exception.getMessage());
            }
    }
}