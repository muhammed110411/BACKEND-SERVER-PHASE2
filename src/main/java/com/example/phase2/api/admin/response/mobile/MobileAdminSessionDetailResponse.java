package com.example.phase2.api.admin.response.mobile;

import com.example.phase2.api.admin.response.IngestionAnomalyFlagResponse;
import com.example.phase2.api.admin.response.SessionEscalationResponse;
import com.example.phase2.api.admin.response.SessionEventResponse;
import com.example.phase2.api.admin.response.SessionScorePointResponse;
import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;
import java.util.List;

public record MobileAdminSessionDetailResponse(
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
        MobileAdminSessionPenaltiesResponse penalties,
        List<SessionEventResponse> events,
        List<SessionEscalationResponse> escalations,
        List<SessionScorePointResponse> scoreHistory,
        List<IngestionAnomalyFlagResponse> ingestionIssues
) {
}
