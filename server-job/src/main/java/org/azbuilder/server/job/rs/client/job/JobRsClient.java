package org.azbuilder.server.job.rs.client.job;

import org.azbuilder.server.job.rs.model.job.JobSearchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "azb-server-job",url = "http://localhost:8080")
public interface JobRsClient {

    @RequestMapping(method = RequestMethod.GET, value="/api/v1/organization/{organizationId}/job")
    JobSearchResponse getAllJobs(@RequestParam("organizationId") String organizationId, @RequestParam(required = false) String status);
}
