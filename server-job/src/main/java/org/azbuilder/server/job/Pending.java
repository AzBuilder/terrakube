package org.azbuilder.server.job;

import lombok.extern.slf4j.Slf4j;
import org.azbuilder.server.job.rs.RsClient;
import org.azbuilder.server.job.rs.model.job.Job;
import org.azbuilder.server.job.rs.model.organization.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Pending {

    @Autowired
    RsClient rsClient;

    @Scheduled(fixedDelay = 10000)
    public void pendingJobs() {
        for (Organization organization : rsClient.getOrganizationRsClient().getAllOrganizations("pending").getData()) {
            log.info("Pending Jobs OrganizationId: {}", organization.getId());

            for (Job job : rsClient.getJobRsClient().getAllJobs(organization.getId(), "pending").getData()) {
                log.info("Pending JobId: {}", job.getId());
                log.info("Pending workspaceId: {}", job.getRelationships().getWorkspace().getData().getId());

            }
        }
    }
}
