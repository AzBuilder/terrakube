package org.azbuilder.server.job.rs.client.organization;

import org.azbuilder.server.job.rs.model.organization.OrganizationSearchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "azb-server-organization",url = "http://localhost:8080")
public interface OrganizationRsClient {

    @RequestMapping(method = RequestMethod.GET, value="/api/v1/organization")
    OrganizationSearchResponse getAllOrganizations(@RequestParam(name = "job.status", required = false) String jobStatus);
}
