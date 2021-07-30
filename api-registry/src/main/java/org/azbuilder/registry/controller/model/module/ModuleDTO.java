package org.azbuilder.registry.controller.model.module;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ModuleDTO {

    private List<VersionsDTO> modules;
}
