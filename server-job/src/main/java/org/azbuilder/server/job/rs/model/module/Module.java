package org.azbuilder.server.job.rs.model.module;

import lombok.Getter;
import lombok.Setter;
import org.azbuilder.server.job.rs.model.generic.Data;

import java.util.HashMap;

@Getter
@Setter
public class Module extends Data {
    HashMap<String, String> attributes;
    Relationships relationships;
}
