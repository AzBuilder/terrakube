package org.azbuilder.api.plugin.vcs.provider;

import org.azbuilder.api.plugin.vcs.provider.github.GitHubTokenException;

public interface GetAccessToken {

    String getAccessToken(String clientId, String clientSecret, String tempCode) throws GitHubTokenException;
}
