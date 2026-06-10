package com.example.phase2.domain.enums;

public enum AdminAccountStatus {
    ACTIVE,
    INACTIVE,
    DEACTIVATED,
    SUSPENDED;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean canAuthenticate() {
        return this == ACTIVE;
    }
}
