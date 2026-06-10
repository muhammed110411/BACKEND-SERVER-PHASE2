package com.example.phase2.persistence.entity;

import com.example.phase2.domain.enums.AdminAccountStatus;
import com.example.phase2.domain.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "admin_users")
public class AdminUserEntity {

    @Id
    @Column(nullable = false, updatable = false, length = 128)
    private String id;

    @Column(nullable = false, unique = true, length = 255)
    private String username;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private AdminAccountStatus accountStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private UserRole role;

    @Column(nullable = false)
    private long createdAt;

    @Column(nullable = false)
    private long updatedAt;

    @Column
    private Long lastLoginAt;

    @Column
    private Long deactivatedAt;

    protected AdminUserEntity() {
    }

    public AdminUserEntity(
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
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash must not be null");
        this.accountStatus = Objects.requireNonNull(accountStatus, "accountStatus must not be null");
        this.role = Objects.requireNonNull(role, "role must not be null");
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastLoginAt = lastLoginAt;
        this.deactivatedAt = deactivatedAt;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = Objects.requireNonNull(username, "username must not be null");
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash must not be null");
    }

    public AdminAccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AdminAccountStatus accountStatus) {
        this.accountStatus = Objects.requireNonNull(accountStatus, "accountStatus must not be null");
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = Objects.requireNonNull(role, "role must not be null");
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Long lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Long getDeactivatedAt() {
        return deactivatedAt;
    }

    public void setDeactivatedAt(Long deactivatedAt) {
        this.deactivatedAt = deactivatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AdminUserEntity that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
