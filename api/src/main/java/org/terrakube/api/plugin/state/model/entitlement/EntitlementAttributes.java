package org.terrakube.api.plugin.state.model.entitlement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntitlementAttributes {
    boolean operations;
    boolean privateModuleRegistry;
    boolean sentinel;
    boolean stateStorage;
    boolean teams;
    boolean VCSIntegrations;
}
