package org.azbuilder.server.job.rs.model.organization.workspace;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WorkspaceResponse {
    List<Workspace> data;
}
