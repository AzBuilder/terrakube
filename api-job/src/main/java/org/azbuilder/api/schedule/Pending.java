package org.azbuilder.api.schedule;

import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.client.RestClient;
import org.azbuilder.api.client.model.organization.job.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Pending {

    @Autowired
    RestClient restClient;

    //@Scheduled(fixedDelay = 10000)
    public void pendingJobs() {

        for (Job job : restClient.getAllOrganizationsWithJobStatus("pending").getIncluded()) {
            log.info("Pending Job: {} WorkspaceId: {}", job.getId(),job.getRelationships().getWorkspace().getData().getId());
        }
    }
}
