package io.terrakube.registry.controller.model.provider;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GpgPublicKeys {
    private String key_id;
    private String ascii_armor;
    private String trust_signature;
    private String source;
    private String source_url;
}
