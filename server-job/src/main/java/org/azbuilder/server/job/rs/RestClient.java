package org.azbuilder.server.job.rs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
@Getter
@AllArgsConstructor
public class RestClient {

    OrganizationRestClient organization;
    JobRestClient job;
    WorkspaceRestClient workspace;
    VariableRestClient variable;
    SecretRestClient secret;
    EnvironmentRestClient environment;
    ModuleRestClient module;
    DefinitionRestClient definition;
    ParameterRestClient parameter;
}
