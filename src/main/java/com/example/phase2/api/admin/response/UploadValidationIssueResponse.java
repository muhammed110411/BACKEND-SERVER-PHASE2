package com.example.phase2.api.admin.response;

public class UploadValidationIssueResponse {

    private String code;
    private String message;
    private String severity;
    private String fieldName;
    private boolean blocking;

    public UploadValidationIssueResponse() {
    }

    public UploadValidationIssueResponse(
            String code,
            String message,
            String severity,
            String fieldName,
            boolean blocking
    ) {
        this.code = code;
        this.message = message;
        this.severity = severity;
        this.fieldName = fieldName;
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

    public boolean isBlocking() {
        return blocking;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }
}
