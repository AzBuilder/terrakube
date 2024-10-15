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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Service
public class BatchModeServiceImpl implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    ExecutorJob executorJob;

    @Autowired
    ExecutorFlagsProperties executorFlagsProperties;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("Ephemeral mode is enable: {}", executorFlagsProperties.isEphemeral());
        if (executorFlagsProperties.isEphemeral())
            try {
                log.info("Running in ephemeral mode....");
                String batchJob = new String(Base64.getDecoder().decode(executorFlagsProperties.getEphemeralJobData()), StandardCharsets.UTF_8);
                TerraformJob terraformJob = new ObjectMapper().readValue(batchJob, TerraformJob.class);
                log.info("Creating ephemeral job....");
                executorJob.createJob(terraformJob);
            } catch (IOException exception) {
                log.error(exception.getMessage());
            }
    }
}