package io.terrakube.registry.controller.model.provider;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FileDTO {
    private List<String> protocols;
    private String os;
    private String arch;
    private String filename;
    private String download_url;
    private String shasums_url;
    private String shasums_signature_url;
    private String shasum;
    private SigningKeys signing_keys;
}
