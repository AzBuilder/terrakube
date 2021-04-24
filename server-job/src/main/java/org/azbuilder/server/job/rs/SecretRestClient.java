package org.azbuilder.server.job.rs;

import org.azbuilder.server.job.rs.model.organization.workspace.secret.SecretResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "azb-server-secret",url = "http://localhost:8080")
public interface SecretRestClient {

    @RequestMapping(method = RequestMethod.GET, value="/api/v1/organization/{organizationId}/workspace/{workspaceId}/secret")
    SecretResponse getAllVariables(@RequestParam("organizationId") String organizationId, @RequestParam("workspaceId") String workspaceId);
}
