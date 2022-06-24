package org.terrakube.api.plugin.vcs.provider.gitlab;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitLabToken {
    private String access_token;
    private String token_type;
    private int expires_in;
    private String refresh_token;
    private int created_at;
}
