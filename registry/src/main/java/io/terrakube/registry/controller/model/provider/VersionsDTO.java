package io.terrakube.registry.controller.model.provider;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VersionsDTO {
    List<VersionDTO> versions;
}
