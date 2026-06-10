package com.example.phase2.api.common.response;

import java.util.List;

public class ApiErrorResponse {

    private final String code;
    private final String message;
    private final int status;
    private final long timestamp;
    private final String path;
    private final List<FieldValidationErrorResponse> fieldErrors;

    public ApiErrorResponse(
            String code,
            String message,
            int status,
            long timestamp,
            String path,
            List<FieldValidationErrorResponse> fieldErrors
    ) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
        this.path = path;
        this.fieldErrors = fieldErrors;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }

    public List<FieldValidationErrorResponse> getFieldErrors() {
        return fieldErrors;
    }
}
