package com.example.phase2.api.common.response;

import java.util.Objects;

public class FieldValidationErrorResponse {

    private final String field;
    private final String code;
    private final String message;
    private final String rejectedValue;

    public FieldValidationErrorResponse(
            String field,
            String code,
            String message,
            String rejectedValue
    ) {
        this.field = requireField(field);
        this.code = Objects.requireNonNull(code, "code must not be null");
        this.message = Objects.requireNonNull(message, "message must not be null");
        this.rejectedValue = rejectedValue;
    }

    public String getField() {
        return field;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getRejectedValue() {
        return rejectedValue;
    }

    private static String requireField(String field) {
        Objects.requireNonNull(field, "field must not be null");
        if (field.isBlank()) {
            throw new IllegalArgumentException("field must not be blank");
        }
        return field;
    }
}
