package com.example.phase2.domain.model;

import java.util.Objects;

public class IngestionAnomalyFlag {

    private static final String WARNING = "WARNING";
    private static final String ERROR = "ERROR";

    private final String code;
    private final String message;
    private final String severity;
    private final String fieldName;
    private final long detectedAt;
    private final boolean blocksProcessing;

    public IngestionAnomalyFlag(
            String code,
            String message,
            String severity,
            String fieldName,
            long detectedAt,
            boolean blocksProcessing
    ) {
        this.code = requireText(code, "code");
        this.message = requireText(message, "message");
        this.severity = requireSeverity(severity);
        this.fieldName = normalizeOptionalText(fieldName);
        this.detectedAt = detectedAt;
        this.blocksProcessing = blocksProcessing;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getSeverity() {
        return severity;
    }

    public String getFieldName() {
        return fieldName;
    }

    public long getDetectedAt() {
        return detectedAt;
    }

    public boolean isBlocking() {
        return blocksProcessing;
    }

    public boolean appliesToField(String fieldName) {
        if (this.fieldName == null) {
            return fieldName == null;
        }
        return this.fieldName.equals(fieldName);
    }

    public boolean isWarning() {
        return WARNING.equals(severity) && !blocksProcessing;
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }

    private static String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException("fieldName must not be blank");
        }
        return value;
    }

    private static String requireSeverity(String severity) {
        String normalizedSeverity = requireText(severity, "severity");
        if (!WARNING.equals(normalizedSeverity) && !ERROR.equals(normalizedSeverity)) {
            throw new IllegalArgumentException("severity must be WARNING or ERROR");
        }
        return normalizedSeverity;
    }
}
