package io.terrakube.api.plugin.vcs.provider.bitbucket;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BitBucketToken {
    private String scopes;
    private String access_token;
    private int expires_in;
    private String token_type;
    private String state;
    private String refresh_token;
}
