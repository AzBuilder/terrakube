package org.azbuilder.api.plugin.vcs.provider.exception;

public class TokenException extends Exception{
    private final String code;

    public TokenException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
