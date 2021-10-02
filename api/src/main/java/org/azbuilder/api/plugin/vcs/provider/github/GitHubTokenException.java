package org.azbuilder.api.plugin.vcs.provider.github;

public class GitHubTokenException extends Exception{
    private final String code;

    public GitHubTokenException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
