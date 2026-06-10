package com.example.phase2.domain.model;

import com.example.phase2.domain.enums.AdminAccountStatus;
import com.example.phase2.domain.enums.UserRole;

import java.util.Objects;

public class AdminUser {

    private final String id;
    private final String username;
    private final String passwordHash;
    private final AdminAccountStatus accountStatus;
    private final UserRole role;
    private final long createdAt;
    private final long updatedAt;
    private final Long lastLoginAt;
    private final Long deactivatedAt;

    public AdminUser(
            String id,
            String username,
            String passwordHash,
            AdminAccountStatus accountStatus,
            UserRole role,
            long createdAt,
            long updatedAt,
            Long lastLoginAt,
            Long deactivatedAt
    ) {
        this.id = requireText(id, "id");
        this.username = requireText(username, "username");
        this.passwordHash = requireText(passwordHash, "passwordHash");
        this.accountStatus = Objects.requireNonNull(accountStatus, "accountStatus must not be null");
        this.role = Objects.requireNonNull(role, "role must not be null");
        if (this.role != UserRole.ADMIN) {
            throw new IllegalArgumentException("role must be ADMIN for AdminUser");
        }
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastLoginAt = lastLoginAt;
        this.deactivatedAt = deactivatedAt;

        if (this.accountStatus == AdminAccountStatus.DEACTIVATED && this.deactivatedAt == null) {
            throw new IllegalArgumentException("deactivatedAt must not be null when accountStatus is DEACTIVATED");
        }
        if (this.accountStatus != AdminAccountStatus.DEACTIVATED && this.deactivatedAt != null) {
            throw new IllegalArgumentException("deactivatedAt must be null unless accountStatus is DEACTIVATED");
        }
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public AdminAccountStatus getAccountStatus() {
        return accountStatus;
    }

    public UserRole getRole() {
        return role;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public Long getLastLoginAt() {
        return lastLoginAt;
    }

    public Long getDeactivatedAt() {
        return deactivatedAt;
    }

    public boolean isActive() {
        return accountStatus.isActive();
    }

    public boolean canAuthenticate() {
        return accountStatus.canAuthenticate();
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
}
