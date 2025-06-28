package io.terrakube.api.plugin.vcs.provider;

import io.terrakube.api.plugin.vcs.provider.exception.TokenException;

public interface GetAccessToken<T> {

    T getAccessToken(String clientId, String clientSecret, String tempCode, String callback, String endpoint) throws TokenException;
}
