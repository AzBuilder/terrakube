package org.azbuilder.server.job;

import lombok.extern.slf4j.Slf4j;
import org.azbuilder.server.job.rs.RestClient;
import org.azbuilder.server.job.rs.model.generic.Resource;
import org.azbuilder.server.job.rs.model.organization.job.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.azbuilder.server.job.rs.model.organization.Organization;

@Service
@Slf4j
public class Pending {

    @Autowired
    RestClient restClient;

    @Scheduled(fixedDelay = 10000)
    public void pendingJobs() {

        restClient.getOrganization().getAllOrganizations().getIncluded();

        for (Job job : restClient.getOrganization().getAllOrganizationsWithJobStatus("pending").getIncluded()) {
            log.info("Job: {}", job.getId());

            //CALL POD CREATOR SERVICE

            //UPDATE STATUS 
        }
    }
}
