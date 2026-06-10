package com.example.phase2.web.viewmodel;

import com.example.phase2.api.admin.response.DriverAnalyticsResponse;
import com.example.phase2.api.admin.response.DriverDetailResponse;

public class DriverAnalyticsPageViewModel {

    private String driverId;
    private DriverAnalyticsResponse analytics;
    private DriverDetailResponse driverSummary;
    private Long fromDate;
    private Long toDate;
    private String pageTitle;
    private String activeRangeLabel;
    private boolean hasAnalyticsData;
    private String emptyStateMessage;
    private String errorMessage;

    public DriverAnalyticsPageViewModel() {
    }

    public DriverAnalyticsPageViewModel(
            String driverId,
            DriverAnalyticsResponse analytics,
            DriverDetailResponse driverSummary,
            Long fromDate,
            Long toDate,
            String pageTitle,
            String activeRangeLabel,
            boolean hasAnalyticsData,
            String emptyStateMessage
    ) {
        this.driverId = driverId;
        this.analytics = analytics;
        this.driverSummary = driverSummary;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.pageTitle = pageTitle;
        this.activeRangeLabel = activeRangeLabel;
        this.hasAnalyticsData = hasAnalyticsData;
        this.emptyStateMessage = emptyStateMessage;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public DriverAnalyticsResponse getAnalytics() {
        return analytics;
    }

    public void setAnalytics(DriverAnalyticsResponse analytics) {
        this.analytics = analytics;
    }

    public DriverDetailResponse getDriverSummary() {
        return driverSummary;
    }

    public void setDriverSummary(DriverDetailResponse driverSummary) {
        this.driverSummary = driverSummary;
    }

    public Long getFromDate() {
        return fromDate;
    }

    public void setFromDate(Long fromDate) {
        this.fromDate = fromDate;
    }

    public Long getToDate() {
        return toDate;
    }

    public void setToDate(Long toDate) {
        this.toDate = toDate;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getActiveRangeLabel() {
        return activeRangeLabel;
    }

    public void setActiveRangeLabel(String activeRangeLabel) {
        this.activeRangeLabel = activeRangeLabel;
    }

    public boolean isHasAnalyticsData() {
        return hasAnalyticsData;
    }

    public void setHasAnalyticsData(boolean hasAnalyticsData) {
        this.hasAnalyticsData = hasAnalyticsData;
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

    public boolean isHasError() {
        return errorMessage != null && !errorMessage.isBlank();
    }
}
