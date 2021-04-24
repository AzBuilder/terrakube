package org.azbuilder.server.job.rs.model.organization;

import lombok.Getter;
import lombok.Setter;
import org.azbuilder.server.job.rs.model.organization.job.Job;

import java.util.List;

@Getter
@Setter
public class OrganizationResponse<T> {

    List<Organization> data;
    List<T> included;
}
