package org.azbuilder.server.job.rs;

import org.azbuilder.server.job.rs.model.organization.workspace.variable.VariableResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "azb-server-variable",url = "http://localhost:8080")
public interface VariableRestClient {

    @RequestMapping(method = RequestMethod.GET, value="/api/v1/organization/{organizationId}/workspace/{workspaceId}/variable")
    VariableResponse getAllVariables(@RequestParam("organizationId") String organizationId, @RequestParam("workspaceId") String workspaceId);
}
