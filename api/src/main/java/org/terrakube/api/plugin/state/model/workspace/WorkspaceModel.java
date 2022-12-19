package org.terrakube.api.plugin.state.model.workspace;

import lombok.Getter;
import lombok.Setter;
import org.terrakube.api.plugin.state.model.generic.Resource;

@Getter
@Setter
public class WorkspaceModel  extends Resource {
    WorkspaceAttributes attributes;
}
