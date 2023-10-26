package org.terrakube.api.plugin.state.model.entitlement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.terrakube.api.plugin.state.model.generic.Resource;

@Getter
@Setter
@ToString
public class EntitlementData {
    Resource data;
}
