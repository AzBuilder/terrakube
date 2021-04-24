package org.azbuilder.server.job.rs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.azbuilder.server.job.rs.client.job.JobRsClient;
import org.azbuilder.server.job.rs.client.organization.OrganizationRsClient;
import org.azbuilder.server.job.rs.client.module.DefinitionRsClient;
import org.azbuilder.server.job.rs.client.module.ModuleRsClient;
import org.azbuilder.server.job.rs.client.module.ParameterRsClient;
import org.azbuilder.server.job.rs.client.workspace.EnvironmentRsClient;
import org.azbuilder.server.job.rs.client.workspace.SecretRsClient;
import org.azbuilder.server.job.rs.client.workspace.VariableRsClient;
import org.azbuilder.server.job.rs.client.workspace.WorkspaceRsClient;
import org.springframework.stereotype.Service;

@Service
@Getter
@AllArgsConstructor
public class RsClient {

    OrganizationRsClient organizationRsClient;
    JobRsClient jobRsClient;
    WorkspaceRsClient workspaceRsClient;
    VariableRsClient variableRsClient;
    SecretRsClient secretRsClient;
    EnvironmentRsClient environmentRsClient;
    ModuleRsClient moduleRsClient;
    DefinitionRsClient definitionRsClient;
    ParameterRsClient parameterRsClient;
}
