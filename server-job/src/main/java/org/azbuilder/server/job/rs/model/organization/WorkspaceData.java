package org.azbuilder.server.job.rs.model.organization;

import lombok.Getter;
import lombok.Setter;
import org.azbuilder.server.job.rs.model.generic.Data;

import java.util.List;

@Getter
@Setter
public class WorkspaceData {
    List<Data> data;
}
