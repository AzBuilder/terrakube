package io.terrakube.api.plugin.vcs.provider.azdevops;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AzDevOpsToken {
    private String access_token;
    private String token_type;
    private int expires_in;
    private String refresh_token;
}
