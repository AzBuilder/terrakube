package org.terrakube.api.plugin.vcs.provider;

import org.terrakube.api.plugin.vcs.provider.exception.TokenException;

public interface GetAccessToken<T> {

    T getAccessToken(String clientId, String clientSecret, String tempCode, String callback) throws TokenException;
}
