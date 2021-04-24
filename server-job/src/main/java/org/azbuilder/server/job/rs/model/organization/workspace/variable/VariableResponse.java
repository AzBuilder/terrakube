package org.azbuilder.server.job.rs.model.organization.workspace.variable;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VariableResponse {
    List<Variable> data;
}
