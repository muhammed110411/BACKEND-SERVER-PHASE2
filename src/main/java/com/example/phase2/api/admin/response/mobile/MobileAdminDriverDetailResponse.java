package com.example.phase2.api.admin.response.mobile;

import com.example.phase2.domain.enums.DriverAccountStatus;
import com.example.phase2.domain.enums.UserRole;
import java.util.List;

public record MobileAdminDriverDetailResponse(
        String driverId,
        String name,
        String email,
        UserRole role,
        DriverAccountStatus accountStatus,
        int totalSessions,
        double totalDistanceKm,
        double longTermReliabilityScore,
        List<MobileAdminSessionSummaryResponse> recentSessions
) {
}
