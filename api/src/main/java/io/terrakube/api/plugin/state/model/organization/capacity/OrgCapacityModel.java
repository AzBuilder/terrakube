package io.terrakube.api.plugin.state.model.organization.capacity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.terrakube.api.plugin.state.model.generic.Resource;

@Getter
@Setter
@ToString
public class OrgCapacityModel extends Resource {
    OrgCapacityAttributes attributes;
}
