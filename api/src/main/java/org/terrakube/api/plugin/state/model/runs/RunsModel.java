package org.terrakube.api.plugin.state.model.runs;

import lombok.Getter;
import lombok.Setter;
import org.terrakube.api.plugin.state.model.generic.Resource;

import java.util.Map;

@Getter
@Setter
public class RunsModel extends Resource {
    Map<String, Object> attributes;
    Relationships relationships;
}
