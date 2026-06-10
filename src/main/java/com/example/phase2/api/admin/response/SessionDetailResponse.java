package com.example.phase2.api.admin.response;

import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;
import com.example.phase2.domain.enums.UploadProcessingStatus;

import java.util.List;

public class SessionDetailResponse {

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
    private double totalContinuousPenalty;
    private double totalEventPenalty;
    private double totalEscalationPenalty;
    private long totalDurationSeconds;
    private double totalDistanceKm;
    private int totalEventCount;
    private int totalEscalationCount;
    private long uploadedAt;
    private Long processedAt;
    private String sourceClientId;
    private List<SessionEventResponse> events;
    private List<SessionEscalationResponse> escalations;
    private List<SessionScorePointResponse> scoreHistory;
    private List<IngestionAnomalyFlagResponse> anomalyFlags;

    public SessionDetailResponse() {
    }

    public SessionDetailResponse(
            String sessionId,
            String driverId,
            String vehicleId,
            long startTimestamp,
            long endTimestamp,
            SessionEndStatus status,
            SessionValidity validity,
            UploadProcessingStatus uploadProcessingStatus,
            double finalScore,
            double totalContinuousPenalty,
            double totalEventPenalty,
            double totalEscalationPenalty,
            long totalDurationSeconds,
            double totalDistanceKm,
            int totalEventCount,
            int totalEscalationCount,
            long uploadedAt,
            Long processedAt,
            String sourceClientId,
            List<SessionEventResponse> events,
            List<SessionEscalationResponse> escalations,
            List<SessionScorePointResponse> scoreHistory,
            List<IngestionAnomalyFlagResponse> anomalyFlags
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
        this.totalContinuousPenalty = totalContinuousPenalty;
        this.totalEventPenalty = totalEventPenalty;
        this.totalEscalationPenalty = totalEscalationPenalty;
        this.totalDurationSeconds = totalDurationSeconds;
        this.totalDistanceKm = totalDistanceKm;
        this.totalEventCount = totalEventCount;
        this.totalEscalationCount = totalEscalationCount;
        this.uploadedAt = uploadedAt;
        this.processedAt = processedAt;
        this.sourceClientId = sourceClientId;
        this.events = events;
        this.escalations = escalations;
        this.scoreHistory = scoreHistory;
        this.anomalyFlags = anomalyFlags;
    }

    public SessionDetailResponse(
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
            double totalContinuousPenalty,
            double totalEventPenalty,
            double totalEscalationPenalty,
            long totalDurationSeconds,
            double totalDistanceKm,
            int totalEventCount,
            int totalEscalationCount,
            long uploadedAt,
            Long processedAt,
            String sourceClientId,
            List<SessionEventResponse> events,
            List<SessionEscalationResponse> escalations,
            List<SessionScorePointResponse> scoreHistory,
            List<IngestionAnomalyFlagResponse> anomalyFlags
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
                totalContinuousPenalty,
                totalEventPenalty,
                totalEscalationPenalty,
                totalDurationSeconds,
                totalDistanceKm,
                totalEventCount,
                totalEscalationCount,
                uploadedAt,
                processedAt,
                sourceClientId,
                events,
                escalations,
                scoreHistory,
                anomalyFlags
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

    public double getTotalContinuousPenalty() {
        return totalContinuousPenalty;
    }

    public void setTotalContinuousPenalty(double totalContinuousPenalty) {
        this.totalContinuousPenalty = totalContinuousPenalty;
    }

    public double getTotalEventPenalty() {
        return totalEventPenalty;
    }

    public void setTotalEventPenalty(double totalEventPenalty) {
        this.totalEventPenalty = totalEventPenalty;
    }

    public double getTotalEscalationPenalty() {
        return totalEscalationPenalty;
    }

    public void setTotalEscalationPenalty(double totalEscalationPenalty) {
        this.totalEscalationPenalty = totalEscalationPenalty;
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

    public String getSourceClientId() {
        return sourceClientId;
    }

    public void setSourceClientId(String sourceClientId) {
        this.sourceClientId = sourceClientId;
    }

    public List<SessionEventResponse> getEvents() {
        return events;
    }

    public void setEvents(List<SessionEventResponse> events) {
        this.events = events;
    }

    public List<SessionEscalationResponse> getEscalations() {
        return escalations;
    }

    public void setEscalations(List<SessionEscalationResponse> escalations) {
        this.escalations = escalations;
    }

    public List<SessionScorePointResponse> getScoreHistory() {
        return scoreHistory;
    }

    public void setScoreHistory(List<SessionScorePointResponse> scoreHistory) {
        this.scoreHistory = scoreHistory;
    }

    public List<IngestionAnomalyFlagResponse> getAnomalyFlags() {
        return anomalyFlags;
    }

    public void setAnomalyFlags(List<IngestionAnomalyFlagResponse> anomalyFlags) {
        this.anomalyFlags = anomalyFlags;
    }
}
