package org.azbuilder.registry.controller.model.provider;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VersionDTO {
    private String version;
    private List<String> protocols;
    private List<PlatformDTO> platforms;
}
