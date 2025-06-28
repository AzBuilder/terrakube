package io.terrakube.api.rs.ssh;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SshType {
    rsa("id_rsa"),
    ed25519("id_ed25519");

    @Getter
    private String fileName;

}
