package org.azbuilder.server.job.rs.model.job;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JobSearchResponse {

    List<Job> data;
}
