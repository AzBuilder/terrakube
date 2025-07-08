package io.terrakube.api.plugin.state.model.workspace;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WorkspaceList {

    List<WorkspaceModel> data;
}
