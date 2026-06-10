package com.example.phase2.persistence.entity;

import com.example.phase2.domain.enums.DriverAccountStatus;
import com.example.phase2.domain.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "drivers")
public class DriverEntity {

    @Id
    @Column(nullable = false, updatable = false, length = 128)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private DriverAccountStatus accountStatus;

    @Column(nullable = false)
    private double longTermReliabilityScore;

    @Column(nullable = false)
    private int totalSessions;

    @Column(nullable = false)
    private double totalDistanceKm;

    @Column(nullable = false)
    private long createdAt;

    @Column(nullable = false)
    private long updatedAt;

    @Column
    private Long deactivatedAt;

    protected DriverEntity() {
    }

    public DriverEntity(
            String id,
            String name,
            String email,
            String passwordHash,
            UserRole role,
            DriverAccountStatus accountStatus,
            double longTermReliabilityScore,
            int totalSessions,
            double totalDistanceKm,
            long createdAt,
            long updatedAt,
            Long deactivatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.accountStatus = accountStatus;
        setLongTermReliabilityScore(longTermReliabilityScore);
        setTotalSessions(totalSessions);
        setTotalDistanceKm(totalDistanceKm);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deactivatedAt = deactivatedAt;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public DriverAccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(DriverAccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public double getLongTermReliabilityScore() {
        return longTermReliabilityScore;
    }

    public void setLongTermReliabilityScore(double longTermReliabilityScore) {
        if (longTermReliabilityScore < 0.0d) {
            throw new IllegalArgumentException("longTermReliabilityScore must be non-negative");
        }
        this.longTermReliabilityScore = longTermReliabilityScore;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(int totalSessions) {
        if (totalSessions < 0) {
            throw new IllegalArgumentException("totalSessions must be non-negative");
        }
        this.totalSessions = totalSessions;
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public void setTotalDistanceKm(double totalDistanceKm) {
        if (totalDistanceKm < 0.0d) {
            throw new IllegalArgumentException("totalDistanceKm must be non-negative");
        }
        this.totalDistanceKm = totalDistanceKm;
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
        if (!(o instanceof DriverEntity that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
