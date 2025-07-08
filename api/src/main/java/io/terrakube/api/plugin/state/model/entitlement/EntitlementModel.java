package io.terrakube.api.plugin.state.model.entitlement;

import lombok.Getter;
import lombok.Setter;
import io.terrakube.api.plugin.state.model.generic.Resource;

import java.util.Map;

@Getter
@Setter
public class EntitlementModel extends Resource {
    Map<String, Object> attributes;
}
