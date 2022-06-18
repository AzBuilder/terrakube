package org.terrakube.registry.controller.model.provider;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SigningKeys {
    List<GpgPublicKeys> gpg_public_keys;
}
