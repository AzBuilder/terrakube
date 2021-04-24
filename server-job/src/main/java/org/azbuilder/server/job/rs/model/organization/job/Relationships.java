package org.azbuilder.server.job.rs.model.organization.job;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Relationships {

    OrganizationData organization;
    WorkspaceData workspace;
}
