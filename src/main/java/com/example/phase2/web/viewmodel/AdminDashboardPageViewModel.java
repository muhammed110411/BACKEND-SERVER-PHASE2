package com.example.phase2.web.viewmodel;

import com.example.phase2.api.admin.response.DriverCardResponse;
import com.example.phase2.api.admin.response.GlobalAnalyticsResponse;
import com.example.phase2.api.admin.response.SessionSummaryResponse;
import com.example.phase2.api.admin.response.VehicleResponse;
import com.example.phase2.api.common.response.PagedResponse;

import java.util.List;

public class AdminDashboardPageViewModel {

    private final GlobalAnalyticsResponse globalAnalytics;
    private final List<DriverCardResponse> recentDrivers;
    private final List<SessionSummaryResponse> recentSessions;
    private final List<VehicleResponse> recentVehicles;
    private final PagedResponse<DriverCardResponse> driverPage;
    private final PagedResponse<SessionSummaryResponse> sessionPage;
    private final PagedResponse<VehicleResponse> vehiclePage;
    private final int totalDriverCount;
    private final int totalVehicleCount;
    private final int totalSessionCount;
    private final long generatedAt;
    private final String activeFiltersSummary;

    public AdminDashboardPageViewModel(
            GlobalAnalyticsResponse globalAnalytics,
            List<DriverCardResponse> recentDrivers,
            List<SessionSummaryResponse> recentSessions,
            List<VehicleResponse> recentVehicles,
            PagedResponse<DriverCardResponse> driverPage,
            PagedResponse<SessionSummaryResponse> sessionPage,
            PagedResponse<VehicleResponse> vehiclePage,
            int totalDriverCount,
            int totalVehicleCount,
            int totalSessionCount,
            long generatedAt,
            String activeFiltersSummary
    ) {
        this.globalAnalytics = globalAnalytics;
        this.recentDrivers = recentDrivers == null ? List.of() : List.copyOf(recentDrivers);
        this.recentSessions = recentSessions == null ? List.of() : List.copyOf(recentSessions);
        this.recentVehicles = recentVehicles == null ? List.of() : List.copyOf(recentVehicles);
        this.driverPage = driverPage;
        this.sessionPage = sessionPage;
        this.vehiclePage = vehiclePage;
        this.totalDriverCount = totalDriverCount;
        this.totalVehicleCount = totalVehicleCount;
        this.totalSessionCount = totalSessionCount;
        this.generatedAt = generatedAt;
        this.activeFiltersSummary = activeFiltersSummary;
    }

    public GlobalAnalyticsResponse getGlobalAnalytics() {
        return globalAnalytics;
    }

    public List<DriverCardResponse> getRecentDrivers() {
        return recentDrivers;
    }

    public List<SessionSummaryResponse> getRecentSessions() {
        return recentSessions;
    }

    public List<VehicleResponse> getRecentVehicles() {
        return recentVehicles;
    }

    public PagedResponse<DriverCardResponse> getDriverPage() {
        return driverPage;
    }

    public PagedResponse<SessionSummaryResponse> getSessionPage() {
        return sessionPage;
    }

    public PagedResponse<VehicleResponse> getVehiclePage() {
        return vehiclePage;
    }

    public int getTotalDriverCount() {
        return totalDriverCount;
    }

    public int getTotalVehicleCount() {
        return totalVehicleCount;
    }

    public int getTotalSessionCount() {
        return totalSessionCount;
    }

    public long getGeneratedAt() {
        return generatedAt;
    }

    public String getActiveFiltersSummary() {
        return activeFiltersSummary;
    }
}
