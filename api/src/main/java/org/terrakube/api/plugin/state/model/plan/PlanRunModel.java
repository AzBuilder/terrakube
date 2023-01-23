package org.terrakube.api.plugin.state.model.plan;

import lombok.Setter;
import lombok.Getter;
import org.terrakube.api.plugin.state.model.generic.Resource;

import java.util.Map;

@Getter
@Setter
public class PlanRunModel extends Resource {

    Map<String, Object> attributes;
}
