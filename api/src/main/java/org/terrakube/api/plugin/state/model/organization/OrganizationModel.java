package org.terrakube.api.plugin.state.model.organization;

import lombok.Getter;
import lombok.Setter;
import org.terrakube.api.plugin.state.model.generic.Resource;

@Getter
@Setter
public class OrganizationModel extends Resource {
    OrganizationAttributes attributes;
}
