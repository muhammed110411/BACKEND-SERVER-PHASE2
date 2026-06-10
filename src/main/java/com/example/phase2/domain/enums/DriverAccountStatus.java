package com.example.phase2.domain.enums;

public enum DriverAccountStatus {
    ACTIVE,
    INACTIVE,
    DEACTIVATED,
    SUSPENDED;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean canUploadSessions() {
        return this == ACTIVE;
    }
}
