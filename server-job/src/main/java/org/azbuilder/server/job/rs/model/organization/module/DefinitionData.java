package org.azbuilder.server.job.rs.model.organization.module;

import lombok.Getter;
import lombok.Setter;
import org.azbuilder.server.job.rs.model.generic.Resource;

import java.util.List;

@Getter
@Setter
public class DefinitionData {
    List<Resource> data;
}
