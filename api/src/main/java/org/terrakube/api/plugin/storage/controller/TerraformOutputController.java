package org.terrakube.api.plugin.storage.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.terrakube.api.plugin.storage.StorageTypeService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.terrakube.api.plugin.streaming.StreamingService;
import org.terrakube.api.repository.StepRepository;
import org.terrakube.api.rs.job.JobStatus;
import org.terrakube.api.rs.job.step.Step;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@RestController
@Slf4j
@RequestMapping("/tfoutput/v1")
public class TerraformOutputController {

    private StorageTypeService storageTypeService;

    //private StepRepository stepRepository;

    private StreamingService streamingService;

    @Transactional
    @GetMapping(
            value = "/organization/{organizationId}/job/{jobId}/step/{stepId}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public @ResponseBody byte[] getFile(@PathVariable("organizationId") String organizationId, @PathVariable("jobId") String jobId, @PathVariable("stepId") String stepId) {
        //Step currentStep = stepRepository.getReferenceById(UUID.fromString(stepId));
        Optional<String> tempLogs = Optional.ofNullable(streamingService.getCurrentLogs(stepId));
        
        if (!tempLogs.get().isEmpty()) {
            log.info("Reading output from redis stream....");
            
            log.info("Current Logs /n {}", tempLogs.get());
            return tempLogs.get().getBytes(StandardCharsets.UTF_8);
        }
        log.info("Reading output from storage");
        return storageTypeService.getStepOutput(organizationId, jobId, stepId);

    }
}
