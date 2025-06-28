package io.terrakube.api.plugin.state.model.organization;

import lombok.Getter;
import lombok.Setter;
import io.terrakube.api.plugin.state.model.generic.Resource;

import java.util.Map;

@Getter
@Setter
public class OrganizationModel extends Resource {
    Map<String, Object> attributes;
}
