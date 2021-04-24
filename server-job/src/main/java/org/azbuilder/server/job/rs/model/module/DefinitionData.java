package org.azbuilder.server.job.rs.model.module;

import lombok.Getter;
import lombok.Setter;
import org.azbuilder.server.job.rs.model.generic.Data;

import java.util.List;

@Getter
@Setter
public class DefinitionData {
    List<Data> data;
}
