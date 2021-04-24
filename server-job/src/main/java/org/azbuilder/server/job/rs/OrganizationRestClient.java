package org.azbuilder.server.job.rs;

import lombok.NonNull;
import org.azbuilder.server.job.rs.model.organization.OrganizationResponse;
import org.azbuilder.server.job.rs.model.organization.job.Job;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "azb-server-organization",url = "http://localhost:8080")
public interface OrganizationRestClient {

    @RequestMapping(method = RequestMethod.GET, value="/api/v1/organization")
    OrganizationResponse<?> getAllOrganizations();

    @RequestMapping(method = RequestMethod.GET, value="/api/v1/organization?filter[job]=status=={jobStatus}&include=job")
    OrganizationResponse<Job> getAllOrganizationsWithJobStatus(@RequestParam(name = "jobStatus") String jobStatus);

}
