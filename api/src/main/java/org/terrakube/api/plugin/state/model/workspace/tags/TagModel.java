package org.terrakube.api.plugin.state.model.workspace.tags;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.terrakube.api.plugin.state.model.generic.Resource;

import java.util.Map;

@Getter
@Setter
@ToString
public class TagModel extends Resource {
    Map<String, String> attributes;
}
