package org.terrakube.api.plugin.state.model.workspace;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WorkspaceError {
    String status;
    String title;
    String detail;
}
