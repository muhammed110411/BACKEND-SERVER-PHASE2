package com.example.phase2.domain.model;

import com.example.phase2.domain.enums.VehicleStatus;

import java.util.Objects;

public class Vehicle {

    private final String id;
    private String displayName;
    private VehicleStatus status;
    private final long createdAt;
    private long updatedAt;
    private Long deactivatedAt;

    public Vehicle(
            String id,
            String displayName,
            VehicleStatus status,
            long createdAt,
            long updatedAt,
            Long deactivatedAt
    ) {
        this.id = requireText(id, "id");
        this.displayName = requireText(displayName, "displayName");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deactivatedAt = deactivatedAt;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public Long getDeactivatedAt() {
        return deactivatedAt;
    }

    public boolean isActive() {
        return deactivatedAt == null;
    }

    public boolean isAvailable() {
        return isActive() && status.isAvailable();
    }

    public boolean isInactive() {
        return deactivatedAt != null;
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
