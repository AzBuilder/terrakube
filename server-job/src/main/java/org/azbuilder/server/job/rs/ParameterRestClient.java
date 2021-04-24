package org.azbuilder.server.job.rs;

import org.azbuilder.server.job.rs.model.organization.module.definition.parameter.ParameterResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "azb-server-parameter", url = "http://localhost:8080")
public interface ParameterRestClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/organization/{organizationId}/module/{moduleId}/definition/{definitionId}/parameter")
    ParameterResponse getAllDefinitions(@RequestParam("organizationId") String organizationId, @RequestParam("moduleId") String moduleId, @RequestParam("definitionId") String definitionId);
}
