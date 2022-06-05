package org.azbuilder.executor.service.mode.online;

import org.azbuilder.executor.service.mode.TerraformJob;
import org.azbuilder.executor.service.executor.ExecutorJob;
import org.azbuilder.executor.service.shutdown.ShutdownServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/terraform-rs")
public class OnlineModeServiceImpl {

    @Autowired
    ExecutorJob executorJob;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)

    public ResponseEntity<TerraformJob> terraformJob(@RequestBody TerraformJob terraformJob) {
        executorJob.createJob(terraformJob);
        return new ResponseEntity<TerraformJob>(terraformJob, HttpStatus.ACCEPTED);
    }
}
