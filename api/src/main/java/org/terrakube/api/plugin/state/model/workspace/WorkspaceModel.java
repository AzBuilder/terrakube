package org.terrakube.api.plugin.state.model.workspace;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.terrakube.api.plugin.state.model.generic.Resource;
import java.util.Map;

@Getter
@Setter
@ToString
public class WorkspaceModel  extends Resource {
    Map<String, Object> attributes;
}
