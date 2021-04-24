package org.azbuilder.server.job.rs;

import org.azbuilder.server.job.rs.model.organization.job.JobResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "azb-server-job",url = "http://localhost:8080")
public interface JobRestClient {

    @RequestMapping(method = RequestMethod.GET, value="/api/v1/organization/{organizationId}/job")
    JobResponse getAllJobs(@RequestParam("organizationId") String organizationId, @RequestParam(required = false) String status);
}
