package com.example.phase2.api.admin.response.mobile;

import java.util.List;

public record MobileAdminDashboardResponse(
        int totalDrivers,
        int activeDrivers,
        int totalVehicles,
        int totalSessions,
        double averageScore,
        List<MobileAdminSessionSummaryResponse> recentSessions
) {
}
