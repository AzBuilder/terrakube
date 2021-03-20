package org.azbuilder.api.workspace.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WorkspaceDTO {

    private String workspaceId;
    private String name;
    private String description;
}
