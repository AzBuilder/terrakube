package org.azbuilder.api.plugin.vcs.provider.github;

public class GitHubTokenException extends Exception{
    private String code;

    public GitHubTokenException(String code, String message) {
        super(message);
        this.setCode(code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
