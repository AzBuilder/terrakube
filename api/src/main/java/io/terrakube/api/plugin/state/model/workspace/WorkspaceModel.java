package io.terrakube.api.plugin.state.model.workspace;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import io.terrakube.api.plugin.state.model.generic.Resource;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@ToString
public class WorkspaceModel  extends Resource {
    Map<String, Object> attributes;
    Relationships relationships;
}
