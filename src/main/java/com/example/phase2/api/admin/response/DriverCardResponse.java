package com.example.phase2.api.admin.response;

import com.example.phase2.domain.enums.DriverAccountStatus;
import com.example.phase2.domain.enums.UserRole;

public class DriverCardResponse {

    private String driverId;
    private String driverName;
    private String email;
    private UserRole role;
    private DriverAccountStatus accountStatus;
    private double longTermReliabilityScore;
    private int totalSessions;
    private double totalDistanceKm;

    public DriverCardResponse() {
    }

    public DriverCardResponse(
            String driverId,
            String driverName,
            String email,
            UserRole role,
            DriverAccountStatus accountStatus,
            double longTermReliabilityScore,
            int totalSessions,
            double totalDistanceKm
    ) {
        this.driverId = driverId;
        this.driverName = driverName;
        this.email = email;
        this.role = role;
        this.accountStatus = accountStatus;
        this.longTermReliabilityScore = longTermReliabilityScore;
        this.totalSessions = totalSessions;
        this.totalDistanceKm = totalDistanceKm;
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
}
