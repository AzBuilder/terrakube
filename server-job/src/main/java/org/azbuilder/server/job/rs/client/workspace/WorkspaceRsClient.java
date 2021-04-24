package org.azbuilder.server.job.rs.client.workspace;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "azb-server-workspace",url = "http://localhost:8080")
public interface WorkspaceRsClient {

    @RequestMapping(method = RequestMethod.GET, value="/api/v1/organization/{organizationId}/workspace")
    String getAllWorkspaces(@RequestParam("organizationId") String organizationId);
}
