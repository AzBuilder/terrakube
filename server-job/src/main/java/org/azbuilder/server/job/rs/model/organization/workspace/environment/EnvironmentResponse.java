package org.azbuilder.server.job.rs.model.organization.workspace.environment;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EnvironmentResponse {
    List<Environment> data;
}
