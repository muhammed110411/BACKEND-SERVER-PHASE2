package com.example.phase2.domain.enums;

public enum VehicleStatus {
    AVAILABLE,
    BUSY,
    OFFLINE,
    UNKNOWN;

    public boolean isAvailable() {
        return this == AVAILABLE;
    }
}
