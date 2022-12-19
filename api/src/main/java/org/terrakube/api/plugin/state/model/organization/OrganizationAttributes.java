package org.terrakube.api.plugin.state.model.organization;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class OrganizationAttributes {
    private String name;
    private Map<String,Boolean> permissions;
}
