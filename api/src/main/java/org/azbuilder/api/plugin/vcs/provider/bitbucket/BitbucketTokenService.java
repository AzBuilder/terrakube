package org.azbuilder.api.plugin.vcs.provider.bitbucket;

import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.vcs.provider.GetAccessToken;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BitbucketTokenService implements GetAccessToken {
    @Override
    public String getAccessToken(String clientId, String clientSecret, String tempCode) {
        return null;
    }
}
