package com.example.phase2.exception;

public class UnauthorizedApiClientException extends RuntimeException {

    private final String clientId;
    private final String errorCode;

    public UnauthorizedApiClientException(String message) {
        super(message);
        this.clientId = null;
        this.errorCode = null;
    }

    public UnauthorizedApiClientException(String message, String clientId, String errorCode) {
        super(message);
        this.clientId = clientId;
        this.errorCode = errorCode;
    }

    public String getClientId() {
        return clientId;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
