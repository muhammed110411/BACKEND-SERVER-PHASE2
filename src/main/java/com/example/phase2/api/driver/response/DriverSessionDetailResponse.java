package com.example.phase2.api.driver.response;

import com.example.phase2.api.admin.response.IngestionAnomalyFlagResponse;
import com.example.phase2.api.admin.response.SessionEscalationResponse;
import com.example.phase2.api.admin.response.SessionEventResponse;
import com.example.phase2.api.admin.response.SessionScorePointResponse;
import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;
import java.util.List;

public class DriverSessionDetailResponse extends DriverSessionSummaryResponse {

    private DriverSessionPenaltiesResponse penalties;
    private List<SessionEventResponse> events;
    private List<SessionEscalationResponse> escalations;
    private List<SessionScorePointResponse> scoreHistory;
    private List<IngestionAnomalyFlagResponse> ingestionIssues;

    public DriverSessionDetailResponse() {
    }

    public DriverSessionDetailResponse(
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
            int totalEscalationCount,
            DriverSessionPenaltiesResponse penalties,
            List<SessionEventResponse> events,
            List<SessionEscalationResponse> escalations,
            List<SessionScorePointResponse> scoreHistory,
            List<IngestionAnomalyFlagResponse> ingestionIssues
    ) {
        super(
                sessionId,
                driverId,
                driverName,
                vehicleId,
                vehicleName,
                finalScore,
                validity,
                status,
                startTimestamp,
                endTimestamp,
                uploadedAt,
                totalEventCount,
                totalEscalationCount
        );
        this.penalties = penalties;
        this.events = events;
        this.escalations = escalations;
        this.scoreHistory = scoreHistory;
        this.ingestionIssues = ingestionIssues;
    }

    public DriverSessionPenaltiesResponse getPenalties() {
        return penalties;
    }

    public void setPenalties(DriverSessionPenaltiesResponse penalties) {
        this.penalties = penalties;
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

    public List<IngestionAnomalyFlagResponse> getIngestionIssues() {
        return ingestionIssues;
    }

    public void setIngestionIssues(List<IngestionAnomalyFlagResponse> ingestionIssues) {
        this.ingestionIssues = ingestionIssues;
    }
}
