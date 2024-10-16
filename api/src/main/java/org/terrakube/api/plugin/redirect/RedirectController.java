package org.terrakube.api.plugin.redirect;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.terrakube.api.repository.JobRepository;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/app")
public class RedirectController {
    private String uiURL;
    private JobRepository jobRepository;

    public RedirectController(JobRepository jobRepository, @Value("${org.terrakube.ui.url}") String uiURL){
        this.jobRepository=jobRepository;
        this.uiURL = uiURL;
    }

    @GetMapping(path = "/{organizationName}/{workspaceName}/runs/{jobId}")
    ResponseEntity<Void> jobIdRedirect(@PathVariable("organizationName") String organizationName, @PathVariable("workspaceName") String workspaceName, @PathVariable("jobId") String jobId) {
        log.info("Redirect for: {}/{}/{}", organizationName, workspaceName, jobId);
        int jobIdFixed = Integer.parseInt(jobId.replace("run-",""));
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(String.format("%s/organizations/%s/workspaces/%s/runs/%s", uiURL,jobRepository.findById(jobIdFixed).get().getOrganization().getId(), jobRepository.findById(jobIdFixed).get().getWorkspace().getId(), jobIdFixed)))
                .build();
    }
}
