package io.terrakube.registry.controller.model.module;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VersionsDTO {

    private List<VersionDTO> versions;
}
