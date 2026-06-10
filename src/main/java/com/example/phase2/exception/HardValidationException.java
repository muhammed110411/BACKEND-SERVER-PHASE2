package com.example.phase2.exception;

public class HardValidationException extends RuntimeException {

    private final String errorCode;
    private final String fieldName;
    private final Object rejectedValue;

    public HardValidationException(String message) {
        super(message);
        this.errorCode = null;
        this.fieldName = null;
        this.rejectedValue = null;
    }

    public HardValidationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.fieldName = null;
        this.rejectedValue = null;
    }

    public HardValidationException(
            String message,
            String errorCode,
            String fieldName,
            Object rejectedValue
    ) {
        super(message);
        this.errorCode = errorCode;
        this.fieldName = fieldName;
        this.rejectedValue = rejectedValue;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }
}
