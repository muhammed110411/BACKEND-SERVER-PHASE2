package com.example.phase2.api.admin.response;

import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;
import com.example.phase2.domain.enums.UploadProcessingStatus;

public class SessionSummaryResponse {

    private String sessionId;
    private String driverId;
    private String driverName;
    private String vehicleId;
    private long startTimestamp;
    private long endTimestamp;
    private SessionEndStatus status;
    private SessionValidity validity;
    private UploadProcessingStatus uploadProcessingStatus;
    private double finalScore;
    private long totalDurationSeconds;
    private double totalDistanceKm;
    private int totalEventCount;
    private int totalEscalationCount;
    private long uploadedAt;
    private Long processedAt;

    public SessionSummaryResponse() {
    }

    public SessionSummaryResponse(
            String sessionId,
            String driverId,
            String vehicleId,
            long startTimestamp,
            long endTimestamp,
            SessionEndStatus status,
            SessionValidity validity,
            UploadProcessingStatus uploadProcessingStatus,
            double finalScore,
            long totalDurationSeconds,
            double totalDistanceKm,
            int totalEventCount,
            int totalEscalationCount,
            long uploadedAt,
            Long processedAt
    ) {
        this.sessionId = sessionId;
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.status = status;
        this.validity = validity;
        this.uploadProcessingStatus = uploadProcessingStatus;
        this.finalScore = finalScore;
        this.totalDurationSeconds = totalDurationSeconds;
        this.totalDistanceKm = totalDistanceKm;
        this.totalEventCount = totalEventCount;
        this.totalEscalationCount = totalEscalationCount;
        this.uploadedAt = uploadedAt;
        this.processedAt = processedAt;
    }

    public SessionSummaryResponse(
            String sessionId,
            String driverId,
            String driverName,
            String vehicleId,
            long startTimestamp,
            long endTimestamp,
            SessionEndStatus status,
            SessionValidity validity,
            UploadProcessingStatus uploadProcessingStatus,
            double finalScore,
            long totalDurationSeconds,
            double totalDistanceKm,
            int totalEventCount,
            int totalEscalationCount,
            long uploadedAt,
            Long processedAt
    ) {
        this(
                sessionId,
                driverId,
                vehicleId,
                startTimestamp,
                endTimestamp,
                status,
                validity,
                uploadProcessingStatus,
                finalScore,
                totalDurationSeconds,
                totalDistanceKm,
                totalEventCount,
                totalEscalationCount,
                uploadedAt,
                processedAt
        );
        this.driverName = driverName;
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

    public SessionEndStatus getStatus() {
        return status;
    }

    public void setStatus(SessionEndStatus status) {
        this.status = status;
    }

    public SessionValidity getValidity() {
        return validity;
    }

    public void setValidity(SessionValidity validity) {
        this.validity = validity;
    }

    public UploadProcessingStatus getUploadProcessingStatus() {
        return uploadProcessingStatus;
    }

    public void setUploadProcessingStatus(UploadProcessingStatus uploadProcessingStatus) {
        this.uploadProcessingStatus = uploadProcessingStatus;
    }

    public double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(double finalScore) {
        this.finalScore = finalScore;
    }

    public long getTotalDurationSeconds() {
        return totalDurationSeconds;
    }

    public void setTotalDurationSeconds(long totalDurationSeconds) {
        this.totalDurationSeconds = totalDurationSeconds;
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public void setTotalDistanceKm(double totalDistanceKm) {
        this.totalDistanceKm = totalDistanceKm;
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

    public long getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(long uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public Long getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Long processedAt) {
        this.processedAt = processedAt;
    }
}
