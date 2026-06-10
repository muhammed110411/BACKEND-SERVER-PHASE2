package com.example.phase2.exception;

public class InactiveDriverUploadException extends RuntimeException {

    private final String driverId;
    private final String errorCode;
    private final String accountStatus;

    public InactiveDriverUploadException(String message) {
        super(message);
        this.driverId = null;
        this.errorCode = null;
        this.accountStatus = null;
    }

    public InactiveDriverUploadException(
            String message,
            String driverId,
            String errorCode,
            String accountStatus
    ) {
        super(message);
        this.driverId = driverId;
        this.errorCode = errorCode;
        this.accountStatus = accountStatus;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getAccountStatus() {
        return accountStatus;
    }
}
