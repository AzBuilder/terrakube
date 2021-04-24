package org.azbuilder.server.job.rs.model.organization;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrganizationSearchResponse {

    List<Organization> data;
}
