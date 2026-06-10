package com.example.phase2.api.driver.response;

import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;

public class DriverSessionSummaryResponse {

    private String sessionId;
    private String driverId;
    private String driverName;
    private String vehicleId;
    private String vehicleName;
    private double finalScore;
    private SessionValidity validity;
    private SessionEndStatus status;
    private long startTimestamp;
    private long endTimestamp;
    private long uploadedAt;
    private int totalEventCount;
    private int totalEscalationCount;

    public DriverSessionSummaryResponse() {
    }

    public DriverSessionSummaryResponse(
            String sessionId,
            String driverId,
            String driverName,
            String vehicleId,
            String vehicleName,
            double finalScore,
            SessionValidity validity,
            SessionEndStatus status,
            long startTimestamp,
            long endTimestamp,
            long uploadedAt,
            int totalEventCount,
            int totalEscalationCount
    ) {
        this.sessionId = sessionId;
        this.driverId = driverId;
        this.driverName = driverName;
        this.vehicleId = vehicleId;
        this.vehicleName = vehicleName;
        this.finalScore = finalScore;
        this.validity = validity;
        this.status = status;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.uploadedAt = uploadedAt;
        this.totalEventCount = totalEventCount;
        this.totalEscalationCount = totalEscalationCount;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(double finalScore) {
        this.finalScore = finalScore;
    }

    public SessionValidity getValidity() {
        return validity;
    }

    public void setValidity(SessionValidity validity) {
        this.validity = validity;
    }

    public SessionEndStatus getStatus() {
        return status;
    }

    public void setStatus(SessionEndStatus status) {
        this.status = status;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public long getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(long uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public int getTotalEventCount() {
        return totalEventCount;
    }

    public void setTotalEventCount(int totalEventCount) {
        this.totalEventCount = totalEventCount;
    }

    public int getTotalEscalationCount() {
        return totalEscalationCount;
    }

    public void setTotalEscalationCount(int totalEscalationCount) {
        this.totalEscalationCount = totalEscalationCount;
    }
}
