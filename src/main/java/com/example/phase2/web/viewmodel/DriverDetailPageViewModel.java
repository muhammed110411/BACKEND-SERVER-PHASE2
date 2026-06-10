package com.example.phase2.web.viewmodel;

import com.example.phase2.api.admin.response.DriverDetailResponse;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DriverDetailPageViewModel {

    private static final DateTimeFormatter RECORD_TIMESTAMP_FORMATTER = DateTimeFormatter
            .ofPattern("MMM d, yyyy HH:mm 'UTC'")
            .withZone(ZoneOffset.UTC);

    private DriverDetailResponse driver;
    private String pageTitle;
    private boolean canViewAnalytics;
    private boolean canViewSessions;
    private String emptyStateMessage;
    private String errorMessage;

    public DriverDetailPageViewModel() {
    }

    public DriverDetailPageViewModel(
            DriverDetailResponse driver,
            String pageTitle,
            boolean canViewAnalytics,
            boolean canViewSessions,
            String emptyStateMessage,
            String errorMessage
    ) {
        this.driver = driver;
        this.pageTitle = pageTitle;
        this.canViewAnalytics = canViewAnalytics;
        this.canViewSessions = canViewSessions;
        this.emptyStateMessage = emptyStateMessage;
        this.errorMessage = errorMessage;
    }

    public DriverDetailResponse getDriver() {
        return driver;
    }

    public void setDriver(DriverDetailResponse driver) {
        this.driver = driver;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public boolean isCanViewAnalytics() {
        return canViewAnalytics;
    }

    public void setCanViewAnalytics(boolean canViewAnalytics) {
        this.canViewAnalytics = canViewAnalytics;
    }

    public boolean isCanViewSessions() {
        return canViewSessions;
    }

    public void setCanViewSessions(boolean canViewSessions) {
        this.canViewSessions = canViewSessions;
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

    public boolean hasDriver() {
        return driver != null;
    }

    public boolean hasError() {
        return errorMessage != null && !errorMessage.isBlank();
    }

    public String getDriverInitials() {
        if (driver == null || driver.getDriverName() == null || driver.getDriverName().isBlank()) {
            return "DR";
        }
        String[] parts = driver.getDriverName().trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    public String getScoreBand() {
        if (driver == null) {
            return "score-medium";
        }
        double score = driver.getLongTermReliabilityScore();
        if (score >= 80.0d) {
            return "score-good";
        }
        if (score >= 60.0d) {
            return "score-medium";
        }
        return "score-bad";
    }

    public String getStatusBadgeClass() {
        if (driver == null || driver.getAccountStatus() == null) {
            return "";
        }
        return switch (driver.getAccountStatus()) {
            case ACTIVE -> " success";
            case SUSPENDED -> " danger";
            case DEACTIVATED -> " muted";
            case INACTIVE -> " warning";
        };
    }

    public String getCreatedAtDisplay() {
        return formatEpochSeconds(driver != null ? driver.getCreatedAt() : null);
    }

    public String getUpdatedAtDisplay() {
        return formatEpochSeconds(driver != null ? driver.getUpdatedAt() : null);
    }

    public String getDeactivatedAtDisplay() {
        if (driver == null || driver.getDeactivatedAt() == null) {
            return "Not deactivated";
        }
        return formatEpochSeconds(driver.getDeactivatedAt());
    }

    private String formatEpochSeconds(Long epochSeconds) {
        if (epochSeconds == null) {
            return "-";
        }
        return RECORD_TIMESTAMP_FORMATTER.format(Instant.ofEpochSecond(epochSeconds));
    }
}
