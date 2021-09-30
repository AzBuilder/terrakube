package org.azbuilder.api.plugin.vcs.provider;

public interface GetAccessToken {

    String getAccessToken(String clientId, String clientSecret, String tempCode);
}
