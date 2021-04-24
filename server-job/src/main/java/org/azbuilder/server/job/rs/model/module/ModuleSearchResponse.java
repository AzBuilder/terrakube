package org.azbuilder.server.job.rs.model.module;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ModuleSearchResponse {
    List<Module> data;
}
