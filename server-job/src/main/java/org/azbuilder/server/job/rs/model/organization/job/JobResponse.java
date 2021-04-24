package org.azbuilder.server.job.rs.model.organization.job;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JobResponse {

    List<Job> data;
}
