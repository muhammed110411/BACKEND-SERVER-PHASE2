package com.example.phase2.api.admin.response;

public class IngestionAnomalyFlagResponse {

    private String code;
    private String message;
    private String severity;
    private String fieldName;
    private long detectedAt;
    private boolean blocking;

    public IngestionAnomalyFlagResponse() {
    }

    public IngestionAnomalyFlagResponse(
            String code,
            String message,
            String severity,
            String fieldName,
            long detectedAt,
            boolean blocking
    ) {
        this.code = code;
        this.message = message;
        this.severity = severity;
        this.fieldName = fieldName;
        this.detectedAt = detectedAt;
        this.blocking = blocking;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public long getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(long detectedAt) {
        this.detectedAt = detectedAt;
    }

    public boolean isBlocking() {
        return blocking;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }
}
