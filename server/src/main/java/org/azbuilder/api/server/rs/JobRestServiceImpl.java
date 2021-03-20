package org.azbuilder.api.server.rs;

import org.azbuilder.api.jobs.JobRestService;
import org.azbuilder.api.jobs.model.JobDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class JobRestServiceImpl implements JobRestService {
    @Override
    public ResponseEntity<List<JobDTO>> getAllJobs(String organizationId, String workspaceId) {
        return null;
    }

    @Override
    public ResponseEntity<JobDTO> getJobById(String organizationId, String workspaceId, String jobId) {
        return null;
    }

    @Override
    public void addJob(String organizationId, String workspaceId, @Valid JobDTO jobDTO) {

    }
}
