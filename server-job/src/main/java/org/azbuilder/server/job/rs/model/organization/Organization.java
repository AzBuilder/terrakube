package org.azbuilder.server.job.rs.model.organization;

import lombok.Getter;
import lombok.Setter;
import org.azbuilder.server.job.rs.model.generic.Data;

import java.util.HashMap;

@Getter
@Setter
public class Organization extends Data {
    HashMap<String, String> attributes;
    Relationships relationships;
}
