package com.example.phase2.api.upload.request;

import java.util.List;

import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;

public class UploadSessionRequest {

    private String sessionId;
    private String driverId;
    private String vehicleId;
    private Long startTimestamp;
    private Long endTimestamp;
    private SessionEndStatus sessionEndStatus;
    private SessionValidity sessionValidity;
    private Double finalScore;
    private Double totalContinuousPenalty;
    private Double totalEventPenalty;
    private Double totalEscalationPenalty;
    private Long totalDurationSeconds;
    private Double totalDistanceKm;
    private Integer totalEventCount;
    private Integer totalEscalationCount;
    private List<UploadEventRequest> events;
    private List<UploadEscalationRequest> escalations;
    private List<UploadScorePointRequest> scorePoints;

    public UploadSessionRequest() {
    }

    public UploadSessionRequest(
            String sessionId,
            String driverId,
            String vehicleId,
            Long startTimestamp,
            Long endTimestamp,
            SessionEndStatus sessionEndStatus,
            SessionValidity sessionValidity,
            Double finalScore,
            Double totalContinuousPenalty,
            Double totalEventPenalty,
            Double totalEscalationPenalty,
            Long totalDurationSeconds,
            Double totalDistanceKm,
            Integer totalEventCount,
            Integer totalEscalationCount,
            List<UploadEventRequest> events,
            List<UploadEscalationRequest> escalations,
            List<UploadScorePointRequest> scorePoints
    ) {
        this.sessionId = sessionId;
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.sessionEndStatus = sessionEndStatus;
        this.sessionValidity = sessionValidity;
        this.finalScore = finalScore;
        this.totalContinuousPenalty = totalContinuousPenalty;
        this.totalEventPenalty = totalEventPenalty;
        this.totalEscalationPenalty = totalEscalationPenalty;
        this.totalDurationSeconds = totalDurationSeconds;
        this.totalDistanceKm = totalDistanceKm;
        this.totalEventCount = totalEventCount;
        this.totalEscalationCount = totalEscalationCount;
        this.events = events;
        this.escalations = escalations;
        this.scorePoints = scorePoints;
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

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(Long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public SessionEndStatus getSessionEndStatus() {
        return sessionEndStatus;
    }

    public void setSessionEndStatus(SessionEndStatus sessionEndStatus) {
        this.sessionEndStatus = sessionEndStatus;
    }

    public SessionValidity getSessionValidity() {
        return sessionValidity;
    }

    public void setSessionValidity(SessionValidity sessionValidity) {
        this.sessionValidity = sessionValidity;
    }

    public Double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Double finalScore) {
        this.finalScore = finalScore;
    }

    public Double getTotalContinuousPenalty() {
        return totalContinuousPenalty;
    }

    public void setTotalContinuousPenalty(Double totalContinuousPenalty) {
        this.totalContinuousPenalty = totalContinuousPenalty;
    }

    public Double getTotalEventPenalty() {
        return totalEventPenalty;
    }

    public void setTotalEventPenalty(Double totalEventPenalty) {
        this.totalEventPenalty = totalEventPenalty;
    }

    public Double getTotalEscalationPenalty() {
        return totalEscalationPenalty;
    }

    public void setTotalEscalationPenalty(Double totalEscalationPenalty) {
        this.totalEscalationPenalty = totalEscalationPenalty;
    }

    public Long getTotalDurationSeconds() {
        return totalDurationSeconds;
    }

    public void setTotalDurationSeconds(Long totalDurationSeconds) {
        this.totalDurationSeconds = totalDurationSeconds;
    }

    public Double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public void setTotalDistanceKm(Double totalDistanceKm) {
        this.totalDistanceKm = totalDistanceKm;
    }

    public Integer getTotalEventCount() {
        return totalEventCount;
    }

    public void setTotalEventCount(Integer totalEventCount) {
        this.totalEventCount = totalEventCount;
    }

    public Integer getTotalEscalationCount() {
        return totalEscalationCount;
    }

    public void setTotalEscalationCount(Integer totalEscalationCount) {
        this.totalEscalationCount = totalEscalationCount;
    }

    public List<UploadEventRequest> getEvents() {
        return events;
    }

    public void setEvents(List<UploadEventRequest> events) {
        this.events = events;
    }

    public List<UploadEscalationRequest> getEscalations() {
        return escalations;
    }

    public void setEscalations(List<UploadEscalationRequest> escalations) {
        this.escalations = escalations;
    }

    public List<UploadScorePointRequest> getScorePoints() {
        return scorePoints;
    }

    public void setScorePoints(List<UploadScorePointRequest> scorePoints) {
        this.scorePoints = scorePoints;
    }
}
