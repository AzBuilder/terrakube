package org.terrakube.api.plugin.state.model.workspace.state.consumers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.terrakube.api.plugin.state.model.workspace.WorkspaceModel;

import java.util.List;

@Getter
@Setter
@ToString
public class StateConsumerList {

    List<WorkspaceModel> data;
}
