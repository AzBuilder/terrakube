package org.azbuilder.api.plugin.vcs.provider;

import org.azbuilder.api.plugin.vcs.provider.exception.TokenException;

public interface GetAccessToken<T> {

    T getAccessToken(String clientId, String clientSecret, String tempCode) throws TokenException;
}
