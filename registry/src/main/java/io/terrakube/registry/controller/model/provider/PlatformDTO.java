package io.terrakube.registry.controller.model.provider;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlatformDTO {
    private String os;
    private String arch;
}
