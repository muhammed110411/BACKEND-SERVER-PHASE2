package com.example.phase2.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "api_clients")
public class ApiClientEntity {

    @Id
    @Column(nullable = false, updatable = false, length = 128)
    private String id;

    @Column(nullable = false, unique = true, length = 255)
    private String clientId;

    @Column(nullable = false, length = 255)
    private String clientSecretHash;

    @Column(nullable = false, length = 255)
    private String displayName;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private long createdAt;

    @Column(nullable = false)
    private long updatedAt;

    @Column
    private Long lastUsedAt;

    @Column
    private Long deactivatedAt;

    protected ApiClientEntity() {
    }

    public ApiClientEntity(
            String id,
            String clientId,
            String clientSecretHash,
            String displayName,
            boolean active,
            long createdAt,
            long updatedAt,
            Long lastUsedAt,
            Long deactivatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.clientId = Objects.requireNonNull(clientId, "clientId must not be null");
        this.clientSecretHash = Objects.requireNonNull(clientSecretHash, "clientSecretHash must not be null");
        this.displayName = Objects.requireNonNull(displayName, "displayName must not be null");
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastUsedAt = lastUsedAt;
        this.deactivatedAt = deactivatedAt;
    }

    public String getId() {
        return id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = Objects.requireNonNull(clientId, "clientId must not be null");
    }

    public String getClientSecretHash() {
        return clientSecretHash;
    }

    public void setClientSecretHash(String clientSecretHash) {
        this.clientSecretHash = Objects.requireNonNull(clientSecretHash, "clientSecretHash must not be null");
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = Objects.requireNonNull(displayName, "displayName must not be null");
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public Long getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(Long lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
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
        if (!(o instanceof ApiClientEntity that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
