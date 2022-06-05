package org.azbuilder.registry.controller.model.module;

import lombok.Getter;
import lombok.Setter;
import org.azbuilder.registry.controller.model.module.VersionDTO;

import java.util.List;

@Getter
@Setter
public class VersionsDTO {

    private List<VersionDTO> versions;
}
