package com.example.phase2.domain.model;

import com.example.phase2.domain.enums.DriverAccountStatus;
import com.example.phase2.domain.enums.UserRole;

import java.util.Objects;

public class Driver {

    private final String id;
    private String name;
    private String email;
    private UserRole role;
    private DriverAccountStatus accountStatus;
    private double longTermReliabilityScore;
    private int totalSessions;
    private double totalDistanceKm;
    private final long createdAt;
    private long updatedAt;
    private Long deactivatedAt;

    public Driver(
            String id,
            String name,
            String email,
            UserRole role,
            DriverAccountStatus accountStatus,
            double longTermReliabilityScore,
            int totalSessions,
            double totalDistanceKm,
            long createdAt,
            long updatedAt,
            Long deactivatedAt
    ) {
        this.id = requireText(id, "id");
        this.name = requireText(name, "name");
        this.email = requireText(email, "email");
        this.role = Objects.requireNonNull(role, "role must not be null");
        this.accountStatus = Objects.requireNonNull(accountStatus, "accountStatus must not be null");
        this.longTermReliabilityScore = requireNonNegative(longTermReliabilityScore, "longTermReliabilityScore");
        this.totalSessions = requireNonNegative(totalSessions, "totalSessions");
        this.totalDistanceKm = requireNonNegative(totalDistanceKm, "totalDistanceKm");
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deactivatedAt = deactivatedAt;

        if (this.accountStatus == DriverAccountStatus.DEACTIVATED && this.deactivatedAt == null) {
            throw new IllegalArgumentException("deactivatedAt must not be null when accountStatus is DEACTIVATED");
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public DriverAccountStatus getAccountStatus() {
        return accountStatus;
    }

    public double getLongTermReliabilityScore() {
        return longTermReliabilityScore;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
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
        return accountStatus.isActive();
    }

    public boolean canAcceptSessionUpload() {
        return accountStatus.canUploadSessions();
    }

    public boolean isAdminRole() {
        return role == UserRole.ADMIN;
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }

    private static double requireNonNegative(double value, String fieldName) {
        if (value < 0.0d) {
            throw new IllegalArgumentException(fieldName + " must be greater than or equal to 0.0");
        }
        return value;
    }

    private static int requireNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than or equal to 0");
        }
        return value;
    }
}
