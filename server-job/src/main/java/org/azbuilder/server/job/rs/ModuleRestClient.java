package org.azbuilder.server.job.rs;

import org.azbuilder.server.job.rs.model.organization.module.ModuleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "azb-server-module", url = "http://localhost:8080")
public interface ModuleRestClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/organization/{organizationId}/module")
    ModuleResponse getAllModules(@RequestParam("organizationId") String organizationId);

    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/organization/{organizationId}/workspace/{workspaceId}/module")
    ModuleResponse getModule(@RequestParam("organizationId") String organizationId, @RequestParam("moduleId") String moduleId);
}
