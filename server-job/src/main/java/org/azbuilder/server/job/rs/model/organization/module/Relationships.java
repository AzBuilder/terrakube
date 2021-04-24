package org.azbuilder.server.job.rs.model.organization.module;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Relationships {
    OrganizationData organization;
    DefinitionData definition;
}
