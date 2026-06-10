package com.example.phase2.domain.enums;

public enum UploadProcessingStatus {
    RECEIVED,
    PROCESSED,
    PROCESSED_WITH_WARNINGS,
    REJECTED;

    public boolean isTerminal() {
        return this == PROCESSED || this == PROCESSED_WITH_WARNINGS || this == REJECTED;
    }

    public boolean isSuccessful() {
        return this == PROCESSED || this == PROCESSED_WITH_WARNINGS;
    }
}
