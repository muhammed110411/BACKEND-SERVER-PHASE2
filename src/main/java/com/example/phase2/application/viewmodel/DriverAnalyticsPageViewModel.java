package com.example.phase2.application.viewmodel;

import com.example.phase2.api.admin.response.DriverAnalyticsResponse;
import com.example.phase2.api.admin.response.DriverDetailResponse;

public class DriverAnalyticsPageViewModel {

    private DriverDetailResponse driver;
    private DriverAnalyticsResponse analytics;

    public DriverAnalyticsPageViewModel() {
    }

    public DriverAnalyticsPageViewModel(DriverDetailResponse driver, DriverAnalyticsResponse analytics) {
        this.driver = driver;
        this.analytics = analytics;
    }

    public DriverDetailResponse getDriver() {
        return driver;
    }

    public void setDriver(DriverDetailResponse driver) {
        this.driver = driver;
    }

    public DriverAnalyticsResponse getAnalytics() {
        return analytics;
    }

    public void setAnalytics(DriverAnalyticsResponse analytics) {
        this.analytics = analytics;
    }
}
