package com.example.phase2.api.admin.response.mobile;

import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;

public record MobileAdminSessionSummaryResponse(
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
        int totalEscalationCount
) {
}
