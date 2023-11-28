package org.terrakube.api.plugin.redirect;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.terrakube.api.repository.JobRepository;

import java.net.URI;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/app")
public class RedirectController {

    @Value("${org.terrakube.ui.url}")
    private String uiURL;

    @Autowired
    JobRepository jobRepository;

    @GetMapping(path = "/{organizationName}/{workspaceName}/runs/{jobId}")
    ResponseEntity<Void> jobIdRedirect(@PathVariable("organizationName") String organizationName, @PathVariable("workspaceName") String workspaceName, @PathVariable("jobId") int jobId) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("http://www.yahoo.com"))
                .build();
    }
}
