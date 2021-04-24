package org.azbuilder.server.job.rs.model.organization.workspace.variable;

import lombok.Getter;
import lombok.Setter;
import org.azbuilder.server.job.rs.model.generic.Resource;

import java.util.HashMap;

@Getter
@Setter
public class Variable extends Resource {
    HashMap<String, String> attributes;
    Relationships relationships;
}
