package com.example.phase2.web.viewmodel;

import com.example.phase2.api.admin.response.EscalationCountResponse;
import com.example.phase2.api.admin.response.EventCountResponse;
import com.example.phase2.api.admin.response.GlobalAnalyticsResponse;
import java.util.Comparator;

public class FleetAnalyticsPageViewModel {

    private final GlobalAnalyticsResponse globalAnalytics;
    private final Long fromTimestamp;
    private final Long toTimestamp;
    private final String pageTitle;
    private final String activeRangeLabel;
    private final boolean hasAnalyticsData;
    private final String emptyStateMessage;
    private String errorMessage;

    public FleetAnalyticsPageViewModel(
            GlobalAnalyticsResponse globalAnalytics,
            Long fromTimestamp,
            Long toTimestamp,
            String pageTitle,
            String activeRangeLabel,
            boolean hasAnalyticsData,
            String emptyStateMessage
    ) {
        this.globalAnalytics = globalAnalytics;
        this.fromTimestamp = fromTimestamp;
        this.toTimestamp = toTimestamp;
        this.pageTitle = pageTitle;
        this.activeRangeLabel = activeRangeLabel;
        this.hasAnalyticsData = hasAnalyticsData;
        this.emptyStateMessage = emptyStateMessage;
    }

    public GlobalAnalyticsResponse getGlobalAnalytics() {
        return globalAnalytics;
    }

    public Long getFromTimestamp() {
        return fromTimestamp;
    }

    public Long getToTimestamp() {
        return toTimestamp;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public String getActiveRangeLabel() {
        return activeRangeLabel;
    }

    public boolean isHasAnalyticsData() {
        return hasAnalyticsData;
    }

    public String getEmptyStateMessage() {
        return emptyStateMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isHasError() {
        return errorMessage != null && !errorMessage.isBlank();
    }

    public double getInvalidSessionRate() {
        if (globalAnalytics == null || globalAnalytics.getTotalSessions() <= 0) {
            return 0.0d;
        }
        return (globalAnalytics.getInvalidSessionCount() * 100.0d) / globalAnalytics.getTotalSessions();
    }

    public double getWarningSessionRate() {
        if (globalAnalytics == null || globalAnalytics.getTotalSessions() <= 0) {
            return 0.0d;
        }
        return (globalAnalytics.getProcessedWithWarningsSessionCount() * 100.0d) / globalAnalytics.getTotalSessions();
    }

    public double getCompletedSessionRate() {
        if (globalAnalytics == null || globalAnalytics.getTotalSessions() <= 0) {
            return 0.0d;
        }
        return (globalAnalytics.getCompletedSessionCount() * 100.0d) / globalAnalytics.getTotalSessions();
    }

    public double getActiveDriverShare() {
        if (globalAnalytics == null || globalAnalytics.getTotalDrivers() <= 0) {
            return 0.0d;
        }
        return (globalAnalytics.getActiveDriverCount() * 100.0d) / globalAnalytics.getTotalDrivers();
    }

    public double getBusyVehicleShare() {
        if (globalAnalytics == null || globalAnalytics.getTotalVehicles() <= 0) {
            return 0.0d;
        }
        return (globalAnalytics.getBusyVehicleCount() * 100.0d) / globalAnalytics.getTotalVehicles();
    }

    public String getDominantEventLabel() {
        if (globalAnalytics == null || globalAnalytics.getEventCounts() == null) {
            return null;
        }
        return globalAnalytics.getEventCounts().stream()
                .max(Comparator.comparingInt(EventCountResponse::getCount))
                .filter(event -> event.getCount() > 0)
                .map(event -> event.getEventType().name())
                .orElse(null);
    }

    public int getDominantEventCount() {
        if (globalAnalytics == null || globalAnalytics.getEventCounts() == null) {
            return 0;
        }
        return globalAnalytics.getEventCounts().stream()
                .mapToInt(EventCountResponse::getCount)
                .max()
                .orElse(0);
    }

    public String getDominantEscalationLabel() {
        if (globalAnalytics == null || globalAnalytics.getEscalationCounts() == null) {
            return null;
        }
        return globalAnalytics.getEscalationCounts().stream()
                .max(Comparator.comparingInt(EscalationCountResponse::getCount))
                .filter(escalation -> escalation.getCount() > 0)
                .map(escalation -> escalation.getEscalationType().name())
                .orElse(null);
    }

    public int getDominantEscalationCount() {
        if (globalAnalytics == null || globalAnalytics.getEscalationCounts() == null) {
            return 0;
        }
        return globalAnalytics.getEscalationCounts().stream()
                .mapToInt(EscalationCountResponse::getCount)
                .max()
                .orElse(0);
    }
}
