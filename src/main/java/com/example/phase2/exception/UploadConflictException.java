package com.example.phase2.exception;

public class UploadConflictException extends RuntimeException {

    private final String conflictCode;
    private final String sessionId;
    private final String conflictingResource;
    private final String conflictingField;

    public UploadConflictException(String message) {
        super(message);
        this.conflictCode = null;
        this.sessionId = null;
        this.conflictingResource = null;
        this.conflictingField = null;
    }

    public UploadConflictException(
            String message,
            String conflictCode,
            String sessionId,
            String conflictingResource,
            String conflictingField
    ) {
        super(message);
        this.conflictCode = conflictCode;
        this.sessionId = sessionId;
        this.conflictingResource = conflictingResource;
        this.conflictingField = conflictingField;
    }

    public String getConflictCode() {
        return conflictCode;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getConflictingResource() {
        return conflictingResource;
    }

    public String getConflictingField() {
        return conflictingField;
    }
}
