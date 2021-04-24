package org.azbuilder.server.job.rs.model.organization.workspace;

import lombok.Getter;
import lombok.Setter;
import org.azbuilder.server.job.rs.model.generic.Resource;

import java.util.List;

@Getter
@Setter
public class EnvironmentData {

    List<Resource> data;
}
