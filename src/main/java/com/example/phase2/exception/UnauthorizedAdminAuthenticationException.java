package com.example.phase2.exception;

public class UnauthorizedAdminAuthenticationException extends RuntimeException {

    private final String username;
    private final String errorCode;

    public UnauthorizedAdminAuthenticationException(String message) {
        super(message);
        this.username = null;
        this.errorCode = null;
    }

    public UnauthorizedAdminAuthenticationException(String message, String username, String errorCode) {
        super(message);
        this.username = username;
        this.errorCode = errorCode;
    }

    public String getUsername() {
        return username;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
