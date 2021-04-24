package org.azbuilder.server.job.rs.model.organization.module.definition;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DefinitionResponse {
    List<Definition> data;
}
