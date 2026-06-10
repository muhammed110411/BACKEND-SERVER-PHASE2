package com.example.phase2.application.mapper;

import com.example.phase2.api.admin.response.DriverAnalyticsResponse;
import com.example.phase2.api.admin.response.DriverCardResponse;
import com.example.phase2.api.admin.response.DriverDetailResponse;
import com.example.phase2.api.admin.response.EscalationCountResponse;
import com.example.phase2.api.admin.response.EventCountResponse;
import com.example.phase2.api.admin.response.GlobalAnalyticsResponse;
import com.example.phase2.api.admin.response.IngestionAnomalyFlagResponse;
import com.example.phase2.api.admin.response.ScoreTrendPointResponse;
import com.example.phase2.api.admin.response.SessionDetailResponse;
import com.example.phase2.api.admin.response.SessionEscalationResponse;
import com.example.phase2.api.admin.response.SessionEventResponse;
import com.example.phase2.api.admin.response.SessionScorePointResponse;
import com.example.phase2.api.admin.response.SessionSummaryResponse;
import com.example.phase2.api.admin.response.UploadValidationIssueResponse;
import com.example.phase2.api.admin.response.VehicleResponse;
import com.example.phase2.domain.enums.EscalationType;
import com.example.phase2.domain.enums.EventType;
import com.example.phase2.domain.model.Driver;
import com.example.phase2.domain.model.DrivingSessionRecord;
import com.example.phase2.domain.model.EscalationRecord;
import com.example.phase2.domain.model.IngestionAnomalyFlag;
import com.example.phase2.domain.model.SafetyEventRecord;
import com.example.phase2.domain.model.ScorePointRecord;
import com.example.phase2.domain.model.SessionDetailRecord;
import com.example.phase2.domain.model.Vehicle;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class AdminResponseMapper {

    public DriverCardResponse toDriverCardResponse(Driver driver) {
        if (driver == null) {
            return null;
        }

        return new DriverCardResponse(
                driver.getId(),
                driver.getName(),
                driver.getEmail(),
                driver.getRole(),
                driver.getAccountStatus(),
                driver.getLongTermReliabilityScore(),
                driver.getTotalSessions(),
                driver.getTotalDistanceKm()
        );
    }

    public DriverDetailResponse toDriverDetailResponse(Driver driver) {
        if (driver == null) {
            return null;
        }

        return new DriverDetailResponse(
                driver.getId(),
                driver.getName(),
                driver.getEmail(),
                driver.getAccountStatus(),
                driver.getRole(),
                driver.getLongTermReliabilityScore(),
                driver.getTotalSessions(),
                driver.getTotalDistanceKm(),
                driver.getCreatedAt(),
                driver.getUpdatedAt(),
                driver.getDeactivatedAt()
        );
    }

    public DriverAnalyticsResponse toDriverAnalyticsResponse(
            Driver driver,
            Double averageFinalScore,
            Integer validSessionCount,
            Integer invalidSessionCount,
            Integer completedSessionCount,
            Integer abortedSessionCount,
            Integer totalEventCount,
            Integer totalEscalationCount,
            List<ScoreTrendPointResponse> scoreTrend,
            List<EventCountResponse> eventCounts,
            List<EscalationCountResponse> escalationCounts
    ) {
        if (driver == null) {
            return null;
        }

        return new DriverAnalyticsResponse(
                driver.getId(),
                driver.getLongTermReliabilityScore(),
                driver.getTotalSessions(),
                driver.getTotalDistanceKm(),
                requireDouble(averageFinalScore, "averageFinalScore"),
                requireInt(validSessionCount, "validSessionCount"),
                requireInt(invalidSessionCount, "invalidSessionCount"),
                requireInt(completedSessionCount, "completedSessionCount"),
                requireInt(abortedSessionCount, "abortedSessionCount"),
                requireInt(totalEventCount, "totalEventCount"),
                requireInt(totalEscalationCount, "totalEscalationCount"),
                copyList(scoreTrend),
                copyList(eventCounts),
                copyList(escalationCounts)
        );
    }

    public SessionSummaryResponse toSessionSummaryResponse(DrivingSessionRecord session) {
        return toSessionSummaryResponse(session, null);
    }

    public SessionSummaryResponse toSessionSummaryResponse(DrivingSessionRecord session, String driverName) {
        if (session == null) {
            return null;
        }

        return new SessionSummaryResponse(
                session.getId(),
                session.getDriverId(),
                driverName,
                session.getVehicleId(),
                session.getStartTimestamp(),
                session.getEndTimestamp(),
                session.getStatus(),
                session.getValidity(),
                session.getUploadProcessingStatus(),
                session.getFinalScore(),
                session.getTotalDurationSeconds(),
                session.getTotalDistanceKm(),
                session.getTotalEventCount(),
                session.getTotalEscalationCount(),
                session.getUploadedAt(),
                session.getProcessedAt()
        );
    }

    public SessionDetailResponse toSessionDetailResponse(SessionDetailRecord sessionDetail) {
        return toSessionDetailResponse(sessionDetail, null);
    }

    public SessionDetailResponse toSessionDetailResponse(SessionDetailRecord sessionDetail, String driverName) {
        if (sessionDetail == null) {
            return null;
        }

        DrivingSessionRecord session = sessionDetail.getSession();
        return new SessionDetailResponse(
                session.getId(),
                session.getDriverId(),
                driverName,
                session.getVehicleId(),
                session.getStartTimestamp(),
                session.getEndTimestamp(),
                session.getStatus(),
                session.getValidity(),
                session.getUploadProcessingStatus(),
                session.getFinalScore(),
                session.getTotalContinuousPenalty(),
                session.getTotalEventPenalty(),
                session.getTotalEscalationPenalty(),
                session.getTotalDurationSeconds(),
                session.getTotalDistanceKm(),
                session.getTotalEventCount(),
                session.getTotalEscalationCount(),
                session.getUploadedAt(),
                session.getProcessedAt(),
                session.getSourceClientId(),
                mapSessionEvents(sessionDetail.getEvents()),
                mapSessionEscalations(sessionDetail.getEscalations()),
                mapSessionScoreHistory(sessionDetail.getScoreHistory()),
                mapAnomalyFlags(sessionDetail.getIngestionAnomalyFlags())
        );
    }

    public GlobalAnalyticsResponse toGlobalAnalyticsResponse(
            Integer totalDrivers,
            Integer activeDriverCount,
            Integer inactiveDriverCount,
            Integer suspendedDriverCount,
            Integer deactivatedDriverCount,
            Integer totalVehicles,
            Integer availableVehicleCount,
            Integer busyVehicleCount,
            Integer offlineVehicleCount,
            Integer totalSessions,
            Integer validSessionCount,
            Integer invalidSessionCount,
            Integer completedSessionCount,
            Integer abortedSessionCount,
            Integer processedSessionCount,
            Integer processedWithWarningsSessionCount,
            Integer rejectedSessionCount,
            Double averageFinalScore,
            Double totalDistanceKm,
            Integer totalEventCount,
            Integer totalEscalationCount,
            List<ScoreTrendPointResponse> scoreTrend,
            List<EventCountResponse> eventCounts,
            List<EscalationCountResponse> escalationCounts
    ) {
        return new GlobalAnalyticsResponse(
                requireInt(totalDrivers, "totalDrivers"),
                requireInt(activeDriverCount, "activeDriverCount"),
                requireInt(inactiveDriverCount, "inactiveDriverCount"),
                requireInt(suspendedDriverCount, "suspendedDriverCount"),
                requireInt(deactivatedDriverCount, "deactivatedDriverCount"),
                requireInt(totalVehicles, "totalVehicles"),
                requireInt(availableVehicleCount, "availableVehicleCount"),
                requireInt(busyVehicleCount, "busyVehicleCount"),
                requireInt(offlineVehicleCount, "offlineVehicleCount"),
                requireInt(totalSessions, "totalSessions"),
                requireInt(validSessionCount, "validSessionCount"),
                requireInt(invalidSessionCount, "invalidSessionCount"),
                requireInt(completedSessionCount, "completedSessionCount"),
                requireInt(abortedSessionCount, "abortedSessionCount"),
                requireInt(processedSessionCount, "processedSessionCount"),
                requireInt(processedWithWarningsSessionCount, "processedWithWarningsSessionCount"),
                requireInt(rejectedSessionCount, "rejectedSessionCount"),
                requireDouble(averageFinalScore, "averageFinalScore"),
                requireDouble(totalDistanceKm, "totalDistanceKm"),
                requireInt(totalEventCount, "totalEventCount"),
                requireInt(totalEscalationCount, "totalEscalationCount"),
                copyList(scoreTrend),
                copyList(eventCounts),
                copyList(escalationCounts)
        );
    }

    public EventCountResponse toEventCountResponse(EventType eventType, Integer count) {
        return new EventCountResponse(eventType, requireInt(count, "count"));
    }

    public EscalationCountResponse toEscalationCountResponse(EscalationType escalationType, Integer count) {
        return new EscalationCountResponse(escalationType, requireInt(count, "count"));
    }

    public ScoreTrendPointResponse toScoreTrendPointResponse(Long timestamp, Double averageScore, Integer sessionCount) {
        return new ScoreTrendPointResponse(
                requireLong(timestamp, "timestamp"),
                requireDouble(averageScore, "averageScore"),
                requireInt(sessionCount, "sessionCount")
        );
    }

    public ScoreTrendPointResponse toScoreTrendPointResponse(
            Long timestamp,
            Double averageScore,
            Integer sessionCount,
            String sessionId,
            String driverId,
            String driverName
    ) {
        return new ScoreTrendPointResponse(
                requireLong(timestamp, "timestamp"),
                requireDouble(averageScore, "averageScore"),
                requireInt(sessionCount, "sessionCount"),
                sessionId,
                driverId,
                driverName
        );
    }

    public SessionScorePointResponse toSessionScorePointResponse(ScorePointRecord scorePoint) {
        if (scorePoint == null) {
            return null;
        }

        return new SessionScorePointResponse(
                scorePoint.getTimestamp(),
                scorePoint.getScoreValue()
        );
    }

    public SessionEventResponse toSessionEventResponse(SafetyEventRecord event) {
        if (event == null) {
            return null;
        }

        return new SessionEventResponse(
                event.getId(),
                event.getEventType(),
                event.getTimestamp(),
                event.getSeverity()
        );
    }

    public SessionEscalationResponse toSessionEscalationResponse(EscalationRecord escalation) {
        if (escalation == null) {
            return null;
        }

        return new SessionEscalationResponse(
                escalation.getId(),
                escalation.getEscalationType(),
                escalation.getTriggeredAt(),
                copyList(escalation.getRelatedEventTypes()),
                escalation.getSeverityMultiplier()
        );
    }

    public IngestionAnomalyFlagResponse toIngestionAnomalyFlagResponse(IngestionAnomalyFlag anomalyFlag) {
        if (anomalyFlag == null) {
            return null;
        }

        return new IngestionAnomalyFlagResponse(
                anomalyFlag.getCode(),
                anomalyFlag.getMessage(),
                anomalyFlag.getSeverity(),
                anomalyFlag.getFieldName(),
                anomalyFlag.getDetectedAt(),
                anomalyFlag.isBlocking()
        );
    }

    public VehicleResponse toVehicleResponse(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }

        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getDisplayName(),
                vehicle.getStatus(),
                vehicle.getCreatedAt(),
                vehicle.getUpdatedAt(),
                vehicle.getDeactivatedAt()
        );
    }

    public UploadValidationIssueResponse toUploadValidationIssueResponse(IngestionAnomalyFlag anomalyFlag) {
        if (anomalyFlag == null) {
            return null;
        }

        return new UploadValidationIssueResponse(
                anomalyFlag.getCode(),
                anomalyFlag.getMessage(),
                anomalyFlag.getSeverity(),
                anomalyFlag.getFieldName(),
                anomalyFlag.isBlocking()
        );
    }

    private List<SessionEventResponse> mapSessionEvents(List<SafetyEventRecord> events) {
        if (events == null) {
            return List.of();
        }

        return events.stream()
                .map(this::toSessionEventResponse)
                .toList();
    }

    private List<SessionEscalationResponse> mapSessionEscalations(List<EscalationRecord> escalations) {
        if (escalations == null) {
            return List.of();
        }

        return escalations.stream()
                .map(this::toSessionEscalationResponse)
                .toList();
    }

    private List<SessionScorePointResponse> mapSessionScoreHistory(List<ScorePointRecord> scoreHistory) {
        if (scoreHistory == null) {
            return List.of();
        }

        return scoreHistory.stream()
                .map(this::toSessionScorePointResponse)
                .toList();
    }

    private List<IngestionAnomalyFlagResponse> mapAnomalyFlags(List<IngestionAnomalyFlag> anomalyFlags) {
        if (anomalyFlags == null) {
            return List.of();
        }

        return anomalyFlags.stream()
                .map(this::toIngestionAnomalyFlagResponse)
                .toList();
    }

    private static <T> List<T> copyList(List<T> values) {
        if (values == null) {
            return List.of();
        }
        return new ArrayList<>(values);
    }

    private static int requireInt(Integer value, String fieldName) {
        return Objects.requireNonNull(value, fieldName + " must not be null");
    }

    private static long requireLong(Long value, String fieldName) {
        return Objects.requireNonNull(value, fieldName + " must not be null");
    }

    private static double requireDouble(Double value, String fieldName) {
        return Objects.requireNonNull(value, fieldName + " must not be null");
    }
}
