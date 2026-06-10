package com.example.phase2.web.viewmodel;

import com.example.phase2.api.admin.response.SessionDetailResponse;
import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;
import com.example.phase2.domain.enums.UploadProcessingStatus;

public class SessionDetailPageViewModel {

    private SessionDetailResponse session;
    private String pageTitle;
    private String driverName;
    private String vehicleDisplayName;
    private boolean canViewDriver;
    private boolean canViewVehicle;
    private boolean hasAnomalies;
    private int anomalyCount;
    private String scoreChartLabel;
    private String emptyScoreHistoryMessage;
    private String emptyStateMessage;
    private String errorMessage;

    public SessionDetailPageViewModel() {
    }

    public SessionDetailPageViewModel(
            SessionDetailResponse session,
            String pageTitle,
            String driverName,
            String vehicleDisplayName,
            boolean canViewDriver,
            boolean canViewVehicle,
            boolean hasAnomalies,
            int anomalyCount,
            String scoreChartLabel,
            String emptyScoreHistoryMessage,
            String emptyStateMessage,
            String errorMessage
    ) {
        this.session = session;
        this.pageTitle = pageTitle;
        this.driverName = driverName;
        this.vehicleDisplayName = vehicleDisplayName;
        this.canViewDriver = canViewDriver;
        this.canViewVehicle = canViewVehicle;
        this.hasAnomalies = hasAnomalies;
        this.anomalyCount = anomalyCount;
        this.scoreChartLabel = scoreChartLabel;
        this.emptyScoreHistoryMessage = emptyScoreHistoryMessage;
        this.emptyStateMessage = emptyStateMessage;
        this.errorMessage = errorMessage;
    }

    public SessionDetailResponse getSession() {
        return session;
    }

    public SessionDetailResponse getSessionDetail() {
        return session;
    }

    public void setSession(SessionDetailResponse session) {
        this.session = session;
    }

    public void setSessionDetail(SessionDetailResponse session) {
        this.session = session;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getVehicleDisplayName() {
        return vehicleDisplayName;
    }

    public void setVehicleDisplayName(String vehicleDisplayName) {
        this.vehicleDisplayName = vehicleDisplayName;
    }

    public boolean isCanViewDriver() {
        return canViewDriver;
    }

    public void setCanViewDriver(boolean canViewDriver) {
        this.canViewDriver = canViewDriver;
    }

    public boolean isCanViewVehicle() {
        return canViewVehicle;
    }

    public void setCanViewVehicle(boolean canViewVehicle) {
        this.canViewVehicle = canViewVehicle;
    }

    public boolean isHasAnomalies() {
        return hasAnomalies;
    }

    public void setHasAnomalies(boolean hasAnomalies) {
        this.hasAnomalies = hasAnomalies;
    }

    public int getAnomalyCount() {
        return anomalyCount;
    }

    public void setAnomalyCount(int anomalyCount) {
        this.anomalyCount = anomalyCount;
    }

    public String getScoreChartLabel() {
        return scoreChartLabel;
    }

    public void setScoreChartLabel(String scoreChartLabel) {
        this.scoreChartLabel = scoreChartLabel;
    }

    public String getEmptyScoreHistoryMessage() {
        return emptyScoreHistoryMessage;
    }

    public void setEmptyScoreHistoryMessage(String emptyScoreHistoryMessage) {
        this.emptyScoreHistoryMessage = emptyScoreHistoryMessage;
    }

    public String getEmptyStateMessage() {
        return emptyStateMessage;
    }

    public void setEmptyStateMessage(String emptyStateMessage) {
        this.emptyStateMessage = emptyStateMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean hasSession() {
        return session != null;
    }

    public boolean hasError() {
        return errorMessage != null && !errorMessage.isBlank();
    }

    public int getEventCount() {
        return session != null && session.getEvents() != null ? session.getEvents().size() : 0;
    }

    public int getEscalationCount() {
        return session != null && session.getEscalations() != null ? session.getEscalations().size() : 0;
    }

    public int getScorePointCount() {
        return session != null && session.getScoreHistory() != null ? session.getScoreHistory().size() : 0;
    }

    public boolean hasScoreHistory() {
        return getScorePointCount() > 0;
    }

    public String getFinalScoreBand() {
        if (session == null) {
            return "score-watch";
        }
        double score = session.getFinalScore();
        if (score >= 80.0d) {
            return "score-good";
        }
        if (score >= 60.0d) {
            return "score-watch";
        }
        return "score-low";
    }

    public String getStatusBadgeClass() {
        SessionEndStatus status = session != null ? session.getStatus() : null;
        if (status == null) {
            return " is-muted";
        }
        return status == SessionEndStatus.COMPLETED ? " is-completed" : " is-aborted";
    }

    public String getValidityBadgeClass() {
        SessionValidity validity = session != null ? session.getValidity() : null;
        if (validity == null) {
            return " is-muted";
        }
        return validity == SessionValidity.VALID ? " is-valid" : " is-invalid";
    }

    public String getProcessingBadgeClass() {
        UploadProcessingStatus status = session != null ? session.getUploadProcessingStatus() : null;
        if (status == null) {
            return " is-muted";
        }
        return switch (status) {
            case PROCESSED -> " is-processed";
            case PROCESSED_WITH_WARNINGS -> " is-warning";
            case REJECTED -> " is-rejected";
            case RECEIVED -> " is-received";
        };
    }

    public String getSourceClientDisplay() {
        if (session == null || session.getSourceClientId() == null || session.getSourceClientId().isBlank()) {
            return "Not provided";
        }
        return session.getSourceClientId();
    }

    public String getProcessedAtFallbackLabel() {
        return session != null && session.getProcessedAt() != null ? null : "Not processed";
    }
}
