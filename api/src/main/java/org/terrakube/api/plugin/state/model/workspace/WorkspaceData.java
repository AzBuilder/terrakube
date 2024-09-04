package org.terrakube.api.plugin.state.model.workspace;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@ToString
public class WorkspaceData  {
    WorkspaceModel data;
    List<WorkspaceError> errors;
}
