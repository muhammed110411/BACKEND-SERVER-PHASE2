package com.example.phase2.api.admin.response;

import com.example.phase2.domain.enums.DriverAccountStatus;
import com.example.phase2.domain.enums.UserRole;

public class DriverDetailResponse {

    private String driverId;
    private String driverName;
    private String email;
    private DriverAccountStatus accountStatus;
    private UserRole role;
    private double longTermReliabilityScore;
    private int totalSessions;
    private double totalDistanceKm;
    private long createdAt;
    private long updatedAt;
    private Long deactivatedAt;

    public DriverDetailResponse() {
    }

    public DriverDetailResponse(
            String driverId,
            String driverName,
            String email,
            DriverAccountStatus accountStatus,
            UserRole role,
            double longTermReliabilityScore,
            int totalSessions,
            double totalDistanceKm,
            long createdAt,
            long updatedAt,
            Long deactivatedAt
    ) {
        this.driverId = driverId;
        this.driverName = driverName;
        this.email = email;
        this.accountStatus = accountStatus;
        this.role = role;
        this.longTermReliabilityScore = longTermReliabilityScore;
        this.totalSessions = totalSessions;
        this.totalDistanceKm = totalDistanceKm;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deactivatedAt = deactivatedAt;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public DriverAccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(DriverAccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public double getLongTermReliabilityScore() {
        return longTermReliabilityScore;
    }

    public void setLongTermReliabilityScore(double longTermReliabilityScore) {
        this.longTermReliabilityScore = longTermReliabilityScore;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public void setTotalDistanceKm(double totalDistanceKm) {
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
}
